package ted.gun0912.sleep.data.local

import kotlinx.coroutines.flow.Flow
import ted.gun0912.sleep.dataResource.DataResource
import ted.gun0912.sleep.model.DailySleepStatistic
import ted.gun0912.sleep.model.EnvSensorInfo
import ted.gun0912.sleep.model.HourlySleepStatistic
import ted.gun0912.sleep.model.SensorInfo
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import java.time.LocalDateTime

interface StatisticLocalDataResource {

    suspend fun getRecordDays(sleepType: SleepType): List<LocalDate>

    suspend fun getFirstRecordDay(sleepType: SleepType): LocalDate?

    suspend fun getHourlySleepStatistics(
        sleepType: SleepType,
        date: LocalDate
    ): List<HourlySleepStatistic>

    suspend fun getDailySleepStatistics(
        sleepType: SleepType,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailySleepStatistic>

    suspend fun saveSleepRecord(
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<SensorInfo>
    ): LocalDate

    fun saveSleepEnvRecord(
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<EnvSensorInfo>
    ): LocalDate
}
