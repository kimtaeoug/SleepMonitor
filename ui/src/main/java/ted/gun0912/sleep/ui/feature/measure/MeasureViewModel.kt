package ted.gun0912.sleep.ui.feature.measure

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.component.BluetoothDeviceManager
import ted.gun0912.sleep.component.model.BleResult
import ted.gun0912.sleep.component.model.BluetoothResult
import ted.gun0912.sleep.dataResource.DataResource
import ted.gun0912.sleep.domain.usecase.GetUserIdUseCase
import ted.gun0912.sleep.domain.usecase.SaveSleepEnvRecordUseCase
import ted.gun0912.sleep.domain.usecase.SaveSleepRecordUseCase
import ted.gun0912.sleep.model.EnvSensorInfo
import ted.gun0912.sleep.model.EtcSensorInfo
import ted.gun0912.sleep.model.SensorInfo
import ted.gun0912.sleep.model.VitalSensorInfo
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.eventbus.CorBus
import ted.gun0912.sleep.ui.eventbus.CorEvent
import ted.gun0912.sleep.ui.extension.getNotNull
import ted.gun0912.sleep.ui.extension.ticker
import ted.gun0912.sleep.ui.feature.measure.MeasureViewModel.Event
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MeasureViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bluetoothDeviceManager: BluetoothDeviceManager,
    private val saveSleepRecordUseCase: SaveSleepRecordUseCase,
    private val saveSleepEnvRecordUseCase: SaveSleepEnvRecordUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : BaseViewModel<Event>() {
    private  val logger = LoggerUtil("MeasureViewModel").getInstance()

    var isDeviceInit = false

    private var filePath = ""
    private var userId: String? = ""

    @RequiresApi(Build.VERSION_CODES.O)
    private val sleepStartTime = LocalDateTime.now()

    private val sleepDeviceAddress = savedStateHandle.getNotNull<String>(KEY_SLEEP_DEVICE_ADDRESS)
    private val envDeviceAddress = savedStateHandle.getNotNull<String>(KEY_ENV_DEVICE_ADDRESS)

    @RequiresApi(Build.VERSION_CODES.O)
    val alarmDateTime = getAlarmDateTime(savedStateHandle.getNotNull(KEY_ALARM_TIME))
    @RequiresApi(Build.VERSION_CODES.O)
    val alarmTimeText = DateTimeFormatter.ofPattern("a hh:mm").format(alarmDateTime)

    private val _amPmText = MutableStateFlow<String?>(null)
    val amPmText = _amPmText.asStateFlow()

    private val _timeText = MutableStateFlow<String?>(null)
    val timeText = _timeText.asStateFlow()

    private val _secondText = MutableStateFlow<String?>(null)
    val secondText = _secondText.asStateFlow()

    private val _sleepDurationMinute = MutableStateFlow(0)
    val sleepDurationMinute = _sleepDurationMinute.asStateFlow()

    val sleepDurationText = sleepDurationMinute.map { getSleepDurationText(it) }

    var canShowUI = true
        set(value) {
            field = value
            if (value) {
                launch()
            } else {
                disconnect()
            }
        }

    private val sensorInfoList = mutableListOf<SensorInfo>()
    private val sleepEnvSensorInfoList = mutableListOf<EnvSensorInfo>()

    val sleepSensorInfo = bluetoothDeviceManager.sleepBleResult
        .filterIsInstance<BleResult.Data>()
        .map { it.data }
        .onEach { sensorInfoList.add(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val vitalSensorInfo = sleepSensorInfo.filterIsInstance<VitalSensorInfo>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val vitalStateInfo = bluetoothDeviceManager.sleepBleResult
        .filterIsInstance<BleResult.Data>()
        .map { it.data }
        .filterIsInstance<VitalSensorInfo>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    @RequiresApi(Build.VERSION_CODES.O)
    val envSensorInfo = bluetoothDeviceManager.envBlutoothResult
        .filterIsInstance<BluetoothResult.Data>()
        .map { it.data }
        .onEach {
            sleepEnvSensorInfoList.add(it)
            saveSleepEnvironmentData(it)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val etcSensorInfo = bluetoothDeviceManager.sleepBleResult
        .filterIsInstance<BleResult.Data>()
        .map { it.data }
        .filterIsInstance<EtcSensorInfo>()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val adcPower = etcSensorInfo.value?.adcPower

    var isDeviceRequested = false

    private val _isRecordComplete = MutableStateFlow(false)
    val isRecordComplete = _isRecordComplete.asStateFlow()

    init {
        logger.debug("init is invoked in MeasureViewModel")
        setupCurrentTime()
        logger.debug("setupCurrentTime is invoked in MeasureViewModel")
        subscribeAlarm()
        logger.debug("subscribeAlarm is invoked in MeasureViewModel")
        launch {
            sleepSensorInfo.filterNotNull().firstOrNull() ?: return@launch
            bluetoothDeviceManager.sendCommand("SGD\n", viewModelScope)
        }
        launch {
            userId = getUserIdUseCase.invoke().await()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveSleepEnvironmentData(info : EnvSensorInfo){
        viewModelScope.launch {
            saveSleepEnvRecordUseCase(
                userId!!,
                LocalDateTime.now(),
                LocalDateTime.now(),
                listOf(info)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAlarmDateTime(alarmTime: LocalTime): LocalDateTime {
        val adjustDays = if (LocalTime.now().isBefore(alarmTime)) 0 else 1
        return sleepStartTime.plusDays(adjustDays.toLong())
            .withHour(alarmTime.hour).withMinute(alarmTime.minute).withSecond(0)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCurrentTime() = launch {

        ticker(delay = 1, timeUnit = TimeUnit.SECONDS)
            .map { LocalDateTime.now() }
            .collect {
                if (!it.isBefore(alarmDateTime) && !isRecordComplete.value) {
                    logger.debug("here1");
                    saveSleepRecord()
                }
                if (canShowUI) {
                    _amPmText.value = amPmFormatter.format(it)
                    _timeText.value = timeFormatter.format(it)
                    _secondText.value = secondFormatter.format(it)
                    setupSleepDuration()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSleepDuration() {
        _sleepDurationMinute.value =
            Duration.between(sleepStartTime, LocalDateTime.now()).toMinutes().toInt()
    }

    private fun getSleepDurationText(minuteDuration: Int): String {
        val hour = minuteDuration / 60
        val minute = minuteDuration % 60
        return String.format("%02d:%02d", hour, minute)
    }

    private fun subscribeAlarm() = launch {
        CorBus.collect<CorEvent.Alarm> {
            logger.debug("here2");
            saveSleepRecord()
        }
    }

    fun launch() {
        if (isDeviceRequested) {
            return
        }
        bluetoothDeviceManager.connect(sleepDeviceAddress, envDeviceAddress)
        isDeviceRequested = true
        logger.debug("ViewModel is launched")
    }


    fun disconnect() {
        logger.debug("disconnect is invoked");
        if (!isDeviceRequested) {
            return
        }
        bluetoothDeviceManager.disconnect()
        isDeviceRequested = false
        logger.debug("disconnected")
    }

    fun checkBlueToothState() : Boolean{
        return  bluetoothDeviceManager.envBlutoothResult.value.isConnecting
    }

    fun disconnectEnvDevice() {
        bluetoothDeviceManager.disconnectBluetoothDevice()
    }

    //todo
    suspend fun determineSaveSleepRecord() {
        logger.debug("determineSaveSleepRecord is invoked")
        val sleepDurationMinute = sleepDurationMinute.value
        if (sleepDurationMinute > MIN_SLEEP_MINUTE) {
            saveSleepEnvRecord()
            saveSleepRecord()
        }
    }

    // TODO refactor
    private suspend fun saveSleepRecord() {
        logger.debug("saveSleepRecord is invoked")
        logger.debug("_isRecordComplete.value : ${_isRecordComplete.value}")
        if (isRecordComplete.value) {
            return
        }
        logger.debug("userId : $userId")
        logger.debug("filePath : $filePath")
        //데이터 저장하는 곳
        val recordDay =
            saveSleepRecordUseCase(
                userId!!,
                sleepStartTime,
                LocalDateTime.now(),
                sensorInfoList,
                filePath
            ).await() ?: return
        //str ?: "Unknown"
        //if (str != null) str else "Unknown"
        _isRecordComplete.value = true
        logger.debug("passed _isRecordComplete.value : ${_isRecordComplete.value}")
        logger.debug("recordDay : ${recordDay}")
        CorBus.emit(CorEvent.NewRecord(recordDay))
    }

    // TODO
    private suspend fun saveSleepEnvRecord() {
        logger.debug("saveSleepEnvRecord is invoked")
        if (isRecordComplete.value) {
            return
        }
        saveSleepEnvRecordUseCase(
            userId!!,
            sleepStartTime,
            LocalDateTime.now(),
            sleepEnvSensorInfoList,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createSleepRawFile(folderName: String?){
        logger.debug("createSleepRawFile is invoked")
        logger.debug("folderName : $folderName")
        if (folderName == null) return

        // 파일 작성시간 (현재시간으로)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val now = LocalDateTime.now().format(formatter)
        // 파일 이름
        val filename = "${now}.txt"

        val rawData = sensorInfoList.map { it.rawText }
            .joinToString(separator = " ")

        // 파일 없으면 생성 후 파일안에 내용 저장.
        filePath = writeTextFile(folderName, filename, rawData)

        logger.debug("createSleepRawFile")
    }


    // foldername : 파일 저장할 디렉토리 경로
    // filename : 파일 이름
    // contents : 저장할 내용.
    private fun writeTextFile(foldername: String, filename: String, contents: String): String {
        try {
            logger.debug("writeTextFile is invoked")
            // foldername 경로의 파일 객체 생성 (디렉토리를 가리키고 있고, 해당 경로에 디렉토리가 없어도 File 객체 생성됨.)
            val dir = File(foldername)
            logger.debug("dir : $dir")
            //디렉토리 폴더가 없으면 생성함
            if (!dir.exists()) {
                // 디렉토리 생성.
                // 만들고자 하는 디렉토리의 상위 디렉토리가 존재하지 않을 경우, 생성 불가
                dir.mkdir()
            }
            logger.debug("2")

            // 생성한 FileOutputStream 객체를 통해 파일을 생성, 내용 작성한다.
            // 기존 파일에 내용을 추가 할려면 두번째 인자로 true를 적어 준다. true를 추가해도 없으면 만든다.
            val fos = FileOutputStream("$foldername/$filename", true)
            logger.debug("3")
            // 문자열을 바이트배열로 변환해서 파일에 저장한다.
            fos.write(contents.toByteArray())
            logger.debug("4")
            // 파일 닫기.
            fos.close()
        } catch (e: IOException) {
            logger.debug("5")
            e.printStackTrace()
        }
        return "$foldername/$filename"
    }


    sealed interface Event : ViewEvent {
        data class ShowAlarm(val alarmTime: LocalTime) : Event
    }

    companion object {
        const val KEY_SLEEP_DEVICE_ADDRESS = "KEY_SLEEP_DEVICE_ADDRESS"
        const val KEY_ENV_DEVICE_ADDRESS = "KEY_ENV_DEVICE_ADDRESS"

        const val KEY_ALARM_TIME = "KEY_ALARM_TIME"
        @RequiresApi(Build.VERSION_CODES.O)
        private val amPmFormatter = DateTimeFormatter.ofPattern("a")
        @RequiresApi(Build.VERSION_CODES.O)
        private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm")
        @RequiresApi(Build.VERSION_CODES.O)
        private val secondFormatter = DateTimeFormatter.ofPattern("ss")
        private const val MIN_SLEEP_MINUTE = 30
    }

}
