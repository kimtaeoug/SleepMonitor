package ted.gun0912.sleep.data.impl


import kotlinx.coroutines.flow.Flow
import ted.gun0912.sleep.data.bound.flowDataResource
import ted.gun0912.sleep.data.local.StatisticLocalDataResource
import ted.gun0912.sleep.data.remote.StatisticRemoteDataResource
import ted.gun0912.sleep.data.remote.UserRemoteDataSource
import ted.gun0912.sleep.dataResource.DataResource
import ted.gun0912.sleep.domain.repository.StatisticRepository
import ted.gun0912.sleep.model.DailySleepStatistic
import ted.gun0912.sleep.model.EnvSensorInfo
import ted.gun0912.sleep.model.HourlySleepStatistic
import ted.gun0912.sleep.model.SensorInfo
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject


internal class StatisticRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val statisticLocalDataResource: StatisticLocalDataResource,
    private val statisticRemoteDataResource: StatisticRemoteDataResource
) : StatisticRepository {

    override fun getRecordDays(sleepType: SleepType): Flow<DataResource<List<LocalDate>>> =
        flowDataResource {
            statisticLocalDataResource.getRecordDays(sleepType)
        }

    override fun getFirstRecordDay(sleepType: SleepType): Flow<DataResource<LocalDate?>> =
        flowDataResource {
            statisticLocalDataResource.getFirstRecordDay(sleepType)
        }

    override fun getHourlySleepStatistics(
        sleepType: SleepType,
        date: LocalDate
    ): Flow<DataResource<List<HourlySleepStatistic>>> =
        flowDataResource {
            statisticLocalDataResource.getHourlySleepStatistics(sleepType, date)
        }

    override fun getDailySleepStatistics(
        userId: String,
        sleepType: SleepType,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<DataResource<List<DailySleepStatistic>>> = flowDataResource {
        // todo remote + local list data
        val dailyStatistics =
            statisticRemoteDataResource.getDailySleepStatistics(userId, sleepType, startDate, endDate)
                .getOrNull(0)

        statisticLocalDataResource.getDailySleepStatistics(sleepType, startDate, endDate)
            .map {
                it.copy(
                    sleepInterviewScore = dailyStatistics?.sleepInterviewScore,
                    painInterviewScore = dailyStatistics?.painInterviewScore,
                    avgTvoc = dailyStatistics?.avgTvoc,
                    avgTemperature = dailyStatistics?.avgTemperature,
                    avgHumidity = dailyStatistics?.avgHumidity,
                    avgCo2 = dailyStatistics?.avgCo2
                )
            }
    }

    override fun saveSleepRecord(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<SensorInfo>,
        filePath: String // TODO
    ): Flow<DataResource<LocalDate>> = flowDataResource {
        //server
        userRemoteDataSource.saveSleepRecords(userId, sleepTime, wakeUpTime, filePath)
        //local
        statisticLocalDataResource.saveSleepRecord(sleepTime, wakeUpTime, records)
    }

    override suspend fun saveSleepEnvRecord(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<EnvSensorInfo>
    ) {
        //수면 이벤트 서버에만 저장
        userRemoteDataSource.saveSleepEnvRecords(userId, sleepTime, wakeUpTime, records)
    }

}
