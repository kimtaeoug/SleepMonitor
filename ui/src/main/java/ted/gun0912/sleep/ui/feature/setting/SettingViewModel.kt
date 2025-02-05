package ted.gun0912.sleep.ui.feature.setting

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.component.Device
import ted.gun0912.sleep.component.VersionName
import ted.gun0912.sleep.domain.usecase.CheckShowVitalInfoUseCase
import ted.gun0912.sleep.domain.usecase.GetMyDeviceUseCase
import ted.gun0912.sleep.domain.usecase.GetUserUseCase
import ted.gun0912.sleep.domain.usecase.SaveMyDeviceUseCase
import ted.gun0912.sleep.domain.usecase.SaveShowVitalInfoUseCase
import ted.gun0912.sleep.domain.usecase.UpdateUserUseCase
import ted.gun0912.sleep.model.BluetoothDevice
import ted.gun0912.sleep.model.Gender
import ted.gun0912.sleep.model.User
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.extension.requireValue
import ted.gun0912.sleep.ui.feature.setting.SettingViewModel.Event
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val checkShowVitalInfoUseCase: CheckShowVitalInfoUseCase,
    private val saveShowVitalInfoUseCase: SaveShowVitalInfoUseCase,
    val versionName: VersionName,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    val device: Device,
    private val getMyDeviceUseCase: GetMyDeviceUseCase,
    private val saveMyDeviceUseCase: SaveMyDeviceUseCase,
) : BaseViewModel<Event>() {

    private val _showVitalInfo = MutableStateFlow(true)
    val showVitalInfo = _showVitalInfo.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()
    val sleepTimeText = user.map { DateTimeFormatter.ofPattern("a hh:mm").format(it.sleepTime) }

    private val _myDevice = MutableStateFlow<BluetoothDevice?>(null)
    val myDevice = _myDevice.asStateFlow()

    init {
        launch {
            _showVitalInfo.value = checkShowVitalInfoUseCase().await() ?: return@launch
        }
        refreshUser()
        refreshMyDeviceName()
    }

    fun setShowVitalInfo(needShow: Boolean) = launch {
        saveShowVitalInfoUseCase(needShow).await()
        _showVitalInfo.value = needShow
    }

    fun changeDevice() = launch {
        event(Event.ChangeDevice)
    }

    fun showGuide() {
        event(Event.ShowGuide)
    }

    fun refreshUser() = launch {
        val savedUser = getUserUseCase().await()
        logd("savedUser: $savedUser")
        if (savedUser == null) {
            event(Event.ShowProfileInput)
        } else {
            _user.value = savedUser
        }
    }

    fun changeGender() {
        event(Event.ChangeGender(user.requireValue.gender) {
            setUser(user.requireValue.copy(gender = it))
        })
    }

    fun changeBirthYear() {
        event(Event.ChangeBirthYear(user.requireValue.birthYear) {
            setUser(user.requireValue.copy(birthYear = it))
        })
    }

    fun changeSleepTime() {
        event(Event.ChangeSleepTime(user.requireValue.sleepTime) {
            val user = user.requireValue
            if (user.needSleepTimeNotification) {
                event(Event.SetupSleepTimeNotification(it))
            }
            setUser(user.copy(sleepTime = it))
        })
    }

    private fun setUser(user: User) = launch {
        _user.value = user
        updateUserUseCase(user).await()
    }

    fun setSleepTimeNotification(needAlarm: Boolean) = launch {
        val user = user.requireValue
        if (needAlarm) {
            event(Event.SetupSleepTimeNotification(user.sleepTime))
        } else {
            event(Event.CancelSleepTimeNotification)
        }
        setUser(user.copy(needSleepTimeNotification = needAlarm))
    }

    private fun refreshMyDeviceName() = launch {
        _myDevice.value = getMyDeviceUseCase().await() ?: return@launch
    }

    fun changeDeviceName() = launch {
        val deviceName = myDevice.value?.name ?: return@launch
        event(Event.ChangeDeviceName(deviceName, ::saveMyDeviceName))
    }

    private fun saveMyDeviceName(newDeviceName: String) = launch {
        val newDevice = myDevice.value?.copy(name = newDeviceName) ?: return@launch
        saveMyDeviceUseCase(newDevice).await()
        refreshMyDeviceName()
    }

    sealed interface Event : ViewEvent {
        object ChangeDevice : Event
        data class ChangeGender(
            val gender: Gender,
            val completeListener: (Gender) -> Unit
        ) : Event

        data class ChangeBirthYear(
            val birthYear: Int,
            val completeListener: (Int) -> Unit
        ) : Event

        data class ChangeSleepTime(
            val sleepTime: LocalTime,
            val completeListener: (LocalTime) -> Unit
        ) : Event

        object ShowProfileInput : Event

        object CancelSleepTimeNotification : Event
        data class SetupSleepTimeNotification(val sleepTime: LocalTime) : Event
        object ShowGuide : Event
        data class ChangeDeviceName(
            val deviceName: String,
            val completeListener: (String) -> Unit
        ) : Event
    }
}
