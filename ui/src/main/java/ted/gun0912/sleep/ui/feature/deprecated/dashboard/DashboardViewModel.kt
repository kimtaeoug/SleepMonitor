package ted.gun0912.sleep.ui.feature.deprecated.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.domain.usecase.GetDailySleepStatisticListUseCase
import ted.gun0912.sleep.domain.usecase.GetFirstRecordDayUseCase
import ted.gun0912.sleep.model.DailySleepStatistic
import ted.gun0912.sleep.model.RangeDayType
import ted.gun0912.sleep.model.SleepType
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.eventbus.CorBus
import ted.gun0912.sleep.ui.eventbus.CorEvent
import ted.gun0912.sleep.ui.extension.getNotNull
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFirstRecordDayUseCase: GetFirstRecordDayUseCase,
    private val getDailySleepStatisticListUseCase: GetDailySleepStatisticListUseCase,
) : BaseViewModel<DashboardViewModel.Event>() {

    val rangeDayType: RangeDayType = savedStateHandle.getNotNull(KEY_RANGE_DAY_TYPE)
    private val sleepType: SleepType = savedStateHandle.getNotNull(KEY_SLEEP_TYPE)

    private val _dailySleepStatistics = MutableStateFlow<List<DailySleepStatistic>?>(null)
    val dailySleepStatistics = _dailySleepStatistics.asStateFlow()

    private val firstRecordDay = getFirstRecordDayUseCase(sleepType).stateFlow(null) { it }

    private val _selectedDays = MutableStateFlow(getRangeDays(LocalDate.now(), 0))
    val selectedDays = _selectedDays.asStateFlow()

    val selectedDaysText =
        selectedDays.map { (startDate, endDate) -> getSelectedDaysText(startDate, endDate) }

    val canMovePre = combine(selectedDays, firstRecordDay)
    { selectedDays: Pair<LocalDate, LocalDate>, firstRecordDay: LocalDate? ->
        val (startDate, _) = selectedDays
        val firstLocalDate = firstRecordDay ?: LocalDate.now()
        startDate.isAfter(firstLocalDate)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val canMoveNext =
        selectedDays.map { (_, endDate) -> endDate.isBefore(LocalDate.now()) }

    init {
        launch {
            selectedDays.collect { fetchDailySleepStatistics() }
        }
        subscribeNewRecord()
    }

    private fun fetchDailySleepStatistics() = launch {
        val (startDate, endDate) = selectedDays.value
//        _dailySleepStatistics.value =
//            getDailySleepStatisticListUseCase(userId, sleepType, startDate, endDate).await()
    }

    private fun subscribeNewRecord() = launch {
        CorBus.collect<CorEvent.NewRecord> {
            val newRecordDay = it.recordDay
            logd("새로운 기록 추가됨: $newRecordDay")
            val (startDate, endDate) = selectedDays.value
            if (!newRecordDay.isBefore(startDate) && !newRecordDay.isAfter(endDate)) {
                logd("현재 보고 있는 통계주간이므로 새로고침")
                fetchDailySleepStatistics()
            }
        }
    }

    private fun move(value: Int) {
        val startDate = selectedDays.value.first
        _selectedDays.value = getRangeDays(startDate, value)
    }

    fun moveNext() = move(1)
    fun movePre() = move(-1)

    private fun getRangeDays(startDate: LocalDate, value: Int): Pair<LocalDate, LocalDate> =
        when (rangeDayType) {
            RangeDayType.WEEK -> getWeekRangeDays(startDate, value)
            RangeDayType.MONTH -> getMonthRangeDays(startDate, value)
        }

    private fun getWeekRangeDays(startDate: LocalDate, value: Int): Pair<LocalDate, LocalDate> {
        val moveWeekStartDate = startDate.plusWeeks(value.toLong())
        val firstDayOfWeek = moveWeekStartDate.with(DayOfWeek.MONDAY)
        val lastDayOfWeek = firstDayOfWeek.plusDays(6)
        return firstDayOfWeek to lastDayOfWeek
    }

    private fun getMonthRangeDays(startDate: LocalDate, value: Int): Pair<LocalDate, LocalDate> {
        val firstDayOfMonth = startDate.with(TemporalAdjusters.firstDayOfMonth())
        val newStartDateOfMonth = firstDayOfMonth.plusMonths(value.toLong())
        val newEndDateOfMonth = newStartDateOfMonth.with(TemporalAdjusters.lastDayOfMonth())
        return newStartDateOfMonth to newEndDateOfMonth
    }

    private fun getSelectedDaysText(startDate: LocalDate, endDate: LocalDate): String =
        when (rangeDayType) {
            RangeDayType.WEEK -> getSelectedWeekText(startDate, endDate)
            RangeDayType.MONTH -> getSelectedMonthText(startDate)
        }

    private fun getSelectedWeekText(startDate: LocalDate, endDate: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val startDateText = startDate.format(dateFormatter)
        val endDateText = endDate.format(dateFormatter)
        return "$startDateText ~ $endDateText"
    }

    private fun getSelectedMonthText(startDate: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM")
        return startDate.format(dateFormatter)
    }

    sealed interface Event : ViewEvent {

    }


    companion object {
        const val KEY_RANGE_DAY_TYPE = "KEY_RANGE_DAY_TYPE"
        const val KEY_SLEEP_TYPE = "KEY_SLEEP_TYPE"
    }
}
