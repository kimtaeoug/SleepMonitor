package ted.gun0912.sleep.local.util

import ted.gun0912.sleep.model.HourlySleepStatistic
import ted.gun0912.sleep.model.SensorInfo
import ted.gun0912.sleep.model.VitalSensorState
import ted.gun0912.sleep.model.partition
import java.time.Duration
import java.time.LocalDateTime

object HourlySleepStatisticUtil {

    private const val HALF_HOUR_SECONDS = 1800

    fun getHourlySleepStatistics(
        age: Int,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        rawText: String
    ): List<HourlySleepStatistic> {

        val list = rawText.lines().mapNotNull { SensorInfo.from(it) }
        val sleepDuration = Duration.between(sleepTime, wakeUpTime)

        val size = (sleepDuration.seconds / HALF_HOUR_SECONDS).toInt()
        val result = list.chunked(HALF_HOUR_SECONDS)

        val hourlySleepStatistics = mutableListOf<HourlySleepStatistic>()
        for (halfHour in 0..size) {
            val recordDateTime = sleepTime.plusMinutes(30L * halfHour)
            val targetList = result.getOrNull(halfHour) ?: break
            val hourlySleepStatistic = getHourlySleepStatistic(age, recordDateTime, targetList)
            hourlySleepStatistics.add(hourlySleepStatistic)
        }
        return hourlySleepStatistics
    }

    private fun getHourlySleepStatistic(
        age: Int,
        recordDateTime: LocalDateTime,
        list: List<SensorInfo>
    ): HourlySleepStatistic {
        val (vitalList, etcList) = list.partition()

        // 심박수
        val heartRates = vitalList
            .filter { it.state != VitalSensorState.NONE }
            .map { it.heartRate }
            .filter { it > 0 }
        val minHeartRate = heartRates.minOrNull() ?: 0
        val maxHeartRate = heartRates.maxOrNull() ?: 0
        val avgHeartRate = heartRates.average().toInt()

        // 호흡수
        val respirationRates = vitalList
            .filter { it.state != VitalSensorState.NONE }
            .map { it.respirationRate }
            .filter { it > 0 }
        val minRespirationRate = respirationRates.minOrNull() ?: 0
        val maxRespirationRate = respirationRates.maxOrNull() ?: 0
        val avgRespirationRate = respirationRates.average().toInt()

        val dailySleepStatistic =
            DailySleepStatisticUtil.getDailySleepStatistic(
                age,
                recordDateTime.toLocalDate(),
                recordDateTime,
                recordDateTime,
                list
            )
        // 뒤척임
        val moving = dailySleepStatistic.moving

        return HourlySleepStatistic(
            recordDateTime,
            minHeartRate,
            maxHeartRate,
            avgHeartRate,
            minRespirationRate,
            maxRespirationRate,
            avgRespirationRate,
            moving,
        )
    }


}
