package ted.gun0912.sleep.data.remote

import ted.gun0912.sleep.model.DailySleepStatistic
import ted.gun0912.sleep.model.HourlySleepStatistic
import ted.gun0912.sleep.model.SensorInfo
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import java.time.LocalDateTime

interface StatisticRemoteDataResource {

    suspend fun getRecordDays(sleepType: SleepType): List<LocalDate>

    suspend fun getFirstRecordDay(sleepType: SleepType): LocalDate?

    suspend fun getHourlySleepStatistics(
        sleepType: SleepType,
        date: LocalDate
    ): List<HourlySleepStatistic>

    suspend fun getDailySleepStatistics(
        userId: String,
        sleepType: SleepType,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailySleepStatistic>

    suspend fun saveSleepRecord(
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<SensorInfo>
    ): LocalDate
}
