package ted.gun0912.sleep.model

import java.time.LocalDateTime

data class HourlySleepStatistic(
    val recordDateTime: LocalDateTime,

    // 심박수
    val minHeartRate: Int,
    val maxHeartRate: Int,
    val avgHeartRate: Int,

    // 호흡수
    val minRespirationRate: Int,
    val maxRespirationRate: Int,
    val avgRespirationRate: Int,

    // 뒤척임
    val moving: Int,
)

