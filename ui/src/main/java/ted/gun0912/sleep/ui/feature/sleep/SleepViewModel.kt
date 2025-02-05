package ted.gun0912.sleep.ui.feature.sleep

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.component.BluetoothDeviceManager
import ted.gun0912.sleep.component.Haptic
import ted.gun0912.sleep.component.model.BleResult
import ted.gun0912.sleep.component.model.BluetoothResult
import ted.gun0912.sleep.domain.usecase.CheckShowChargePhoneNoticeUseCase
import ted.gun0912.sleep.domain.usecase.CheckShowVitalInfoUseCase
import ted.gun0912.sleep.domain.usecase.GetLastAlarmTimeUseCase
import ted.gun0912.sleep.domain.usecase.SaveLastAlarmTimeUseCase
import ted.gun0912.sleep.model.VitalSensorInfo
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.extension.getNotNull
import ted.gun0912.sleep.ui.feature.sleep.SleepViewModel.Event
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SleepViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bluetoothDeviceManager: BluetoothDeviceManager,
    private val haptic: Haptic,
    private val checkShowVitalInfoUseCase: CheckShowVitalInfoUseCase,
    private val checkShowChargePhoneNoticeUseCase: CheckShowChargePhoneNoticeUseCase,
    getLastAlarmTimeUseCase: GetLastAlarmTimeUseCase,
    private val saveLastAlarmTimeUseCase: SaveLastAlarmTimeUseCase,
) : BaseViewModel<Event>() {
    private val logger : LoggerUtil = LoggerUtil("SleepViewModel").getInstance()
    val sleepDeviceAddress = savedStateHandle.getNotNull<String>(KEY_SLEEP_DEVICE_ADDRESS)
    val envDeviceAddress = savedStateHandle.getNotNull<String>(KEY_ENV_DEVICE_ADDRESS)

    @RequiresApi(Build.VERSION_CODES.O)
    var alarmTime = LocalTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val lastAlarmTime = getLastAlarmTimeUseCase().stateFlow(alarmTime) { it }

    val vitalSensorInfo = bluetoothDeviceManager.sleepBleResult
        .filterIsInstance<BleResult.Data>()
        .map { it.data }
        .filterIsInstance<VitalSensorInfo>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private var hadBeenDisconnected = false
    private var isDeviceRequested = false
    private var isMeasureShowing = false

    var isStartMeasure = false
    var isDeviceInit = false

    val needShowConnecting =
        bluetoothDeviceManager.sleepBleResult.map { it.isConnecting && hadBeenDisconnected && isDeviceRequested }

    private val isSleepDeviceConnected = bluetoothDeviceManager.sleepBleResult.map { it.isRunning }

    private val isSleepDeviceDisconnected = bluetoothDeviceManager.sleepBleResult
        .mapLatest { it.hasBeenDisconnected && isDeviceRequested }
        .onEach {
            if (it) {
                hadBeenDisconnected = it
                logd("$it")
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val isEnvDeviceConnected =
        bluetoothDeviceManager.envBlutoothResult
            .filterIsInstance<BluetoothResult.Connected>()
            .map { true }

    private val isEnvDeviceDisconnected = bluetoothDeviceManager.envBlutoothResult
        .mapLatest { it == BluetoothResult.Disconnected || it == BluetoothResult.NotFound }
        .onEach {
            if (it) {
                hadBeenDisconnected = it
                logd("$it")
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isDeviceConnected = combine(
        isSleepDeviceConnected,
        isEnvDeviceConnected
    ) { isSleepConnected, isEnvConnected ->
        isSleepConnected == true && isEnvConnected == true
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isDeviceDisconnected = combine(
        isSleepDeviceDisconnected,
        isEnvDeviceDisconnected
    ) { isSleepDisconnected, isEnvDisconnected ->
        delay(500)
        isSleepDisconnected || isEnvDisconnected
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _showVitalInfo = MutableStateFlow(true)
    val showVitalInfo = _showVitalInfo.asStateFlow()

    init {
        logger.info("SleepViewModel is init")
        bluetoothDeviceManager.sleepBleResult
            .onEach {
                //logd("$it")
            }.launchIn(viewModelScope)
        isDeviceInit
    }

    fun launch() {
        if (isMeasureShowing) {
            return
        }
        bluetoothDeviceManager.connect(sleepDeviceAddress, envDeviceAddress)
        isDeviceRequested = true
    }

    fun disconnect() {
        if (isMeasureShowing) {
            return
        }
        bluetoothDeviceManager.disconnectBluetoothDevice()
    }

    fun setMeasureShowing(isShowing: Boolean) {
        isMeasureShowing = isShowing
    }

    fun updateVitalInfo() = launch {
        _showVitalInfo.value = checkShowVitalInfoUseCase().await() ?: return@launch
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startSleep() = launch {
        isStartMeasure = true
        saveLastAlarmTimeUseCase(alarmTime)
            .collectDataResource({
                // no-op
            })
        val needShowChargePhoneNotice = checkShowChargePhoneNoticeUseCase().await() ?: return@launch
        if (needShowChargePhoneNotice) {
            event(Event.ShowChargePhone)
        } else {
            event(Event.StartSleep)
        }
    }

    fun disconnectEnvDevice() {
        bluetoothDeviceManager.disconnectBluetoothDevice()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setAlarmTime(hour: Int, minute: Int) {
        haptic.interact()
        alarmTime = LocalTime.of(hour, minute)
    }

    sealed interface Event : ViewEvent {
        object ShowChargePhone : Event
        object StartSleep : Event
    }

    companion object {
        const val KEY_SLEEP_DEVICE_ADDRESS = "KEY_SLEEP_DEVICE_ADDRESS"
        const val KEY_ENV_DEVICE_ADDRESS = "KEY_ENV_DEVICE_ADDRESS"
    }
}
