package ted.gun0912.sleep.domain.repository

import kotlinx.coroutines.flow.Flow
import ted.gun0912.sleep.dataResource.DataResource
import ted.gun0912.sleep.model.DailySleepStatistic
import ted.gun0912.sleep.model.EnvSensorInfo
import ted.gun0912.sleep.model.HourlySleepStatistic
import ted.gun0912.sleep.model.SensorInfo
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import java.time.LocalDateTime

interface StatisticRepository {

    fun getRecordDays(sleepType: SleepType): Flow<DataResource<List<LocalDate>>>

    fun getFirstRecordDay(sleepType: SleepType): Flow<DataResource<LocalDate?>>

    fun getHourlySleepStatistics(
        sleepType: SleepType,
        date: LocalDate
    ): Flow<DataResource<List<HourlySleepStatistic>>>

    fun getDailySleepStatistics(
        userId: String,
        sleepType: SleepType,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<DataResource<List<DailySleepStatistic>>>

    fun saveSleepRecord(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<SensorInfo>,
        filePath: String
    ): Flow<DataResource<LocalDate>>

    suspend fun saveSleepEnvRecord(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<EnvSensorInfo>
    )

}
