package ted.gun0912.sleep.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

data class DailySleepStatistic(
    val recordDay: LocalDate,
    // 평균 심박수
    val heartRate: Int,
    // 잠자리에 든 시간
    val sleepTime: LocalDateTime,
    // 일어난 시간
    val wakeUpTime: LocalDateTime,
    // 수면 효율
    val sleepEfficiency: Int,
    // 총 측정 시간
    val totalMeasureSeconds: Long,
    // 실제 수면 시간
    val realSleepSecond: Int,
    // 호흡장애지수
    val rdi: Int,
    // 수면 점수
    val sleepScore: Int,
    // 뒤척임
    val moving: Int,
    // 뒤척임 지수
    val movingIndex: Int,

    // 환경 센서 수집 데이터
    val avgTemperature: Float? = null,
    val avgCo2: Float? = null,
    val avgHumidity: Float? = null,
    val avgTvoc: Float? = null,

    // 수면 문진 점수
    val sleepInterviewScore: Int? = null,
    // 통증 문진 점수
    val painInterviewScore: Int? = null
) {

    // 실제 수면시간 기간
    val realSleepDuration: Duration
        get() = Duration.ofSeconds(realSleepSecond.toLong())

    //전체 측정시간 기간
    val sleepMeasureDuration: Duration
        get() = Duration.ofSeconds(totalMeasureSeconds)
}

