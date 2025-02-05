package ted.gun0912.sleep.ui.feature.history

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.domain.usecase.GetDailySleepStatisticUseCase
import ted.gun0912.sleep.domain.usecase.GetHourlySleepStatisticListUseCase
import ted.gun0912.sleep.domain.usecase.GetRecordDayListUseCase
import ted.gun0912.sleep.domain.usecase.GetUserIdUseCase
import ted.gun0912.sleep.local.room.dao.SleepMonitorDao
import ted.gun0912.sleep.model.DailySleepStatistic
import ted.gun0912.sleep.model.HourlySleepStatistic
import ted.gun0912.sleep.model.SleepType
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.eventbus.CorBus
import ted.gun0912.sleep.ui.eventbus.CorEvent
import ted.gun0912.sleep.ui.feature.history.HistoryViewModel.Event
import java.time.LocalDate
import java.util.logging.Logger
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getRecordDayListUseCase: GetRecordDayListUseCase,
    private val getDailySleepStatisticUseCase: GetDailySleepStatisticUseCase,
    private val getHourlySleepStatisticListUseCase: GetHourlySleepStatisticListUseCase,
    private val sleepMonitorDao: SleepMonitorDao,
    private val getUserIdUseCase: GetUserIdUseCase
) : BaseViewModel<Event>() {

    var selectedDate: LocalDate? = null
        private set

    private var userId: String? = ""


    var sleepType: SleepType = SleepType.NIGHT

    private val _recordDays = MutableStateFlow<List<LocalDate>?>(null)
    val recordDays = _recordDays.asStateFlow()

    private val _dailySleepStatistic = MutableStateFlow<DailySleepStatistic?>(null)
    val dailySleepStatistic = _dailySleepStatistic.asStateFlow()

    private val _hourlySleepStatistics = MutableStateFlow<List<HourlySleepStatistic>?>(null)
    val hourlySleepStatistics = _hourlySleepStatistics.asStateFlow()

    val avgHeartRate = hourlySleepStatistics.map { list -> list.average { it.avgHeartRate } }
    val minHeartRate = hourlySleepStatistics.map { list -> list.min { it.minHeartRate } }
    val maxHeartRate = hourlySleepStatistics.map { list -> list.max { it.maxHeartRate } }

    val avgRespirationRate =
        hourlySleepStatistics.map { list -> list.average { it.avgRespirationRate } }
    val minRespirationRate =
        hourlySleepStatistics.map { list -> list.min { it.minRespirationRate } }
    val maxRespirationRate =
        hourlySleepStatistics.map { list -> list.max { it.maxRespirationRate } }

    val avgMoving = hourlySleepStatistics.map { list -> list.average { it.moving } }
    val minMoving = hourlySleepStatistics.map { list -> list.min { it.moving } }
    val maxMoving = hourlySleepStatistics.map { list -> list.max { it.moving } }

    private fun List<HourlySleepStatistic>.average(valueGetter: (HourlySleepStatistic) -> Int): Int =
        map(valueGetter).average().toInt()

    private fun List<HourlySleepStatistic>.min(valueGetter: (HourlySleepStatistic) -> Int): Int? =
        map(valueGetter).minOrNull()

    private fun List<HourlySleepStatistic>.max(valueGetter: (HourlySleepStatistic) -> Int): Int? =
        map(valueGetter).maxOrNull()
//    private val logger: LoggerUtil = LoggerUtil().getInstance();

    private val logger: LoggerUtil = LoggerUtil("HISTORY_VIEWMODEL").getInstance();

    init {
        logger.debug("init is invoked in HistoryViewModel")
        fetchRecordDays()
        subscribeNewRecord()
        launch {
            userId = getUserIdUseCase.invoke().await()
        }
    }

    fun changeSleepType(sleepType: SleepType) {
        this.sleepType = sleepType
        fetchRecordDays()
    }

    ///
    /// todo
    /// history 데이터 여기서 fetch
    ///
    fun fetchRecordDays() = launch {
        logger.debug("fetchRecordDays is invoked in HistoryViewModel")
        logger.info("fetchRecordDays\nresult : ${getRecordDayListUseCase(sleepType).await()}")
        _recordDays.value = getRecordDayListUseCase(sleepType).await()
    }

    private fun subscribeNewRecord() = launch { //private YJH0711
        CorBus.collect<CorEvent.NewRecord> {
            logger.debug("subscribeNewRecord is invoked in HistoryViewModel")
            logd("새로운 기록 추가됨")
            fetchRecordDays()
        }
    }

    fun fetchDate(selectedDate: LocalDate) {
        this.selectedDate = selectedDate
        fetchSummary(selectedDate)
        fetchSleepStatistics(selectedDate)
    }

    fun sendRawData() = launch {
        val sleepRecord = selectedDate?.let { sleepMonitorDao.getSleepRecord(sleepType, it) }

        event(Event.RawData(sleepRecord!!.rawText))
    }

    private fun fetchSummary(selectedDate: LocalDate) = launch {
        logger.info("fetchSummary\nresult : ${getDailySleepStatisticUseCase(userId!!, sleepType, selectedDate).await()}")
        _dailySleepStatistic.value = getDailySleepStatisticUseCase(userId!!, sleepType, selectedDate).await()
    }

    private fun fetchSleepStatistics(selectedDate: LocalDate) = launch {
        logger.info("fetchSleepStatistics\nresult : ${getHourlySleepStatisticListUseCase(sleepType, selectedDate).await()}")
        _hourlySleepStatistics.value = getHourlySleepStatisticListUseCase(sleepType, selectedDate).await()
    }

    sealed interface Event : ViewEvent {
        data class RawData(
            val SleepRecord: String
        ) : Event

    }
}

