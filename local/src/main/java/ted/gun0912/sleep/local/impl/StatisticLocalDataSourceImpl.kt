package ted.gun0912.sleep.local.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ted.gun0912.sleep.common.exception.ErrorUtil
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.data.local.StatisticLocalDataResource
import ted.gun0912.sleep.local.model.SleepRecord
import ted.gun0912.sleep.local.pref.PrefUtil
import ted.gun0912.sleep.local.room.dao.SleepMonitorDao
import ted.gun0912.sleep.local.util.DailySleepStatisticUtil
import ted.gun0912.sleep.local.util.HourlySleepStatisticUtil
import ted.gun0912.sleep.model.DailySleepStatistic
import ted.gun0912.sleep.model.EnvSensorInfo
import ted.gun0912.sleep.model.HourlySleepStatistic
import ted.gun0912.sleep.model.SensorInfo
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class StatisticLocalDataSourceImpl @Inject constructor(
    private val sleepMonitorDao: SleepMonitorDao,
    private val prefUtil: PrefUtil,
) : StatisticLocalDataResource {

    private val age: Int
        get() = prefUtil.user?.age ?: run {
            ErrorUtil.handleUnExpectedCaseException { "user 없음" }
            36
        }

    override suspend fun getRecordDays(sleepType: SleepType): List<LocalDate> {
        return sleepMonitorDao.getSleepRecordDates(sleepType)
    }

    override suspend fun getFirstRecordDay(sleepType: SleepType): LocalDate? {
        val firstSleepRecord = sleepMonitorDao.getFirstSleepRecord(sleepType)
        logd("firstSleepRecord?.recordDay: ${firstSleepRecord?.recordDay}")
        return firstSleepRecord?.recordDay
    }

    override suspend fun getHourlySleepStatistics(
        sleepType: SleepType,
        date: LocalDate
    ): List<HourlySleepStatistic> {
        val sleepRecord = sleepMonitorDao.getSleepRecord(sleepType, date) ?: return emptyList()
        val sleepTime = sleepRecord.sleepTime
        val wakeUpTime = sleepRecord.wakeUpTime
        val rawText = sleepRecord.rawText
        logd("sleepTime: $sleepTime")
        logd("wakeUpTime: $wakeUpTime")
        return HourlySleepStatisticUtil.getHourlySleepStatistics(
            age,
            sleepTime,
            wakeUpTime,
            rawText
        )
    }

    override suspend fun getDailySleepStatistics(
        sleepType: SleepType,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailySleepStatistic> =
        sleepMonitorDao.getSleepRecords(sleepType, startDate, endDate)
            .map { sleepRecord ->
                DailySleepStatisticUtil.getDailySleepStatistic(
                    age,
                    sleepRecord.recordDay,
                    sleepRecord.sleepTime,
                    sleepRecord.wakeUpTime,
                    sleepRecord.rawText
                )
            }

    override suspend fun saveSleepRecord(
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<SensorInfo>
    ): LocalDate = withContext(Dispatchers.IO) {
        // 일어난날의 전 날을 잠잔 날로 처리한다
        /**
         * 1. 15일 새벽 1시 취침 / 15일 아침 7시 기상
         * 2. 15일 밤 11시 취침 / 16일 아침 6시 기상
         *
         * 위와 같은 상황에 1,2번이 모두 같은 15일에 잔걸로 계산되서 1개로만 처리되는 문제를 방지하기 위함
         */
        val sleepType = SleepType.fromLocalDateTime(sleepTime)
        val recordDay = when (sleepType) {
            SleepType.DAY -> wakeUpTime
            SleepType.NIGHT -> if (wakeUpTime.hour < 12) wakeUpTime.minusDays(1) else wakeUpTime
        }.toLocalDate()

        val rawText = records.joinToString(""){ it.rawText }

        val sleepRecord = SleepRecord(recordDay, sleepType, sleepTime, wakeUpTime, rawText)

        sleepMonitorDao.insert(sleepRecord)
        recordDay
    }

    override fun saveSleepEnvRecord(
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<EnvSensorInfo>
    ): LocalDate {
        TODO("Not yet implemented")
    }
}
