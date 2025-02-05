package ted.gun0912.sleep.local.util

import android.util.Log
import ted.gun0912.sleep.common.extension.isNull
import ted.gun0912.sleep.model.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.round
import kotlin.math.roundToInt

object DailySleepStatisticUtil {

    private const val SECOND_FACTOR = 2
    private const val MINUTE_4 = 4 * 60 //YJH Test 4*60 5 * 30
    private const val MINUTE_3 = 3 * 60
    private const val MINUTE_10 = 10 * 60;
    private const val MINUTE_20 = 20 * 60;

    private const val NON_THRESHOLD = 0.4

    private var lastEtcSensorInfo: EtcSensorInfo? = null
    private var lastOcommandSensorInfo: OCommandSensorInfo? = null

    // 수면시간 계산 변수
    private var totalSleepSeconds = 0
    private var realSleepSeconds = 0
    private var currentStatus = 0.0
    private var isSleepState_NonMoving = false
    private var isSleepState_AVRHR = false

    private var sleepStartSeconds = 0
    private var sleepStopSeconds = 0

    private var sleepStartSeconds_AVRHR = 0
    private var prevAVRHR_Array = ArrayList<Int>()
    private var prevAVRHR = 0.0
    private var currAVRHR_Array = ArrayList<Int>()
    private var currAVRHR = 0.0

    // 움직임 계산 변수
    private var isSleepState = false
    private var isMoving = false
    private var movingTurn = 0
    private var movingCount = 0

    // RDI 계산 변수
    private var brinValidCount = 0
    private var rdiCount = 0

    fun initValue() {
        lastEtcSensorInfo = null

        totalSleepSeconds = 0
        realSleepSeconds = 0
        sleepStartSeconds_AVRHR = 0
        prevAVRHR_Array.clear()
        currAVRHR_Array.clear()
        prevAVRHR = 0.0
        currAVRHR = 0.0

        currentStatus = 0.0
        //isSleepState_NonMoving = false
        //isSleepState_AVRHR = false

        sleepStartSeconds = 0
        sleepStopSeconds = 0

        isMoving = false
        movingTurn = 0
        movingCount = 0

        brinValidCount = 0
        rdiCount = 0
    }

    fun getDailySleepStatistic(
        age: Int,
        recordDay: LocalDate,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        rawText: String,
    ): DailySleepStatistic = getDailySleepStatistic(
        age,
        recordDay,
        sleepTime,
        wakeUpTime,
        rawText.lines().mapNotNull { SensorInfo.from(it) },
    )

    fun getDailySleepStatistic(
        age: Int,
        recordDay: LocalDate,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        sensorInfoList: List<SensorInfo>,
    ): DailySleepStatistic {

        initValue()
        val vitalList = mutableListOf<VitalSensorInfo>()
        val dCommandSensorInfo: DcommandSensorInfo? =
            sensorInfoList.filterIsInstance<DcommandSensorInfo>().firstOrNull()
        val oCommandSensorInfo: OCommandSensorInfo? =
            sensorInfoList.filterIsInstance<OCommandSensorInfo>().firstOrNull()

        val movPowerThreshold: Int = dCommandSensorInfo?.movPowerThreshold ?: 1500
        val motionThreshold: Int = dCommandSensorInfo?.motionThreshold ?: 500
        val brPThr: Int = oCommandSensorInfo?.brPThr ?: 0
        val hrPThr: Int = oCommandSensorInfo?.hrPThr ?: 0

        for (info in sensorInfoList) {
            if (info is VitalSensorInfo) {
                calculateSleepSecond(info)
                calculateRdi(info, brPThr, hrPThr)
                vitalList.add(info)
            } else if (info is EtcSensorInfo) {
                lastEtcSensorInfo = info
                if (isSleepState_NonMoving || isSleepState_AVRHR) {
                    calculateMoving(info, movPowerThreshold, motionThreshold)
                }
            } else if (info is OCommandSensorInfo) {
                lastOcommandSensorInfo = info
            }
        }

        val heartRate = vitalList.map { it.heartRate }.average()

        val sleepScore = (SleepScoreUtil.getSleepScore2(realSleepSeconds, age)).coerceAtMost(100)

        val movingIndex = (movingTurn * 100f / realSleepSeconds).coerceAtMost(100f)

        val sleepEfficiency = round(sleepScore * 0.8 + (100 - movingIndex) * 0.2)

        val rdi =
            runCatching { rdiCount * 100 / realSleepSeconds }.getOrDefault(
                0
            ).coerceAtMost(100)

        val moving = movingCount

        val totalMeasureSeconds = Duration.between(sleepTime, wakeUpTime).seconds

        return DailySleepStatistic(
            recordDay,
            heartRate.toInt(),
            sleepTime,
            wakeUpTime,
            sleepEfficiency.toInt(),
            totalMeasureSeconds,
            realSleepSeconds,
            rdi,
            sleepScore,
            moving,
            movingIndex.toInt(),
        )
    }

    private fun calculateSleepSecond(vitalSensorInfo: VitalSensorInfo) {
        val state = vitalSensorInfo.state.value
        val heartRate = vitalSensorInfo.heartRate

        currentStatus = if (state >= 3) {
            state.toDouble()
        } else {
            0.1 * state + (1 - 0.1) * currentStatus
        }

        if (!isSleepState_NonMoving) { //20분동안 움직임이 없으면 수면으로 판단
            if (currentStatus > NON_THRESHOLD && currentStatus < 4) { //NON_THRESHOLD = 0.4 1~3일떄 참
                sleepStartSeconds += SECOND_FACTOR
            } else {
                sleepStartSeconds = 0
            }
            if (sleepStartSeconds > MINUTE_20) {
                totalSleepSeconds += MINUTE_20
                realSleepSeconds += MINUTE_20
                isSleepState_NonMoving = true
            }
        }

        if (isSleepState_NonMoving) {
            if (currentStatus < NON_THRESHOLD || currentStatus >= 4) {
                sleepStopSeconds += SECOND_FACTOR
            } else {
                sleepStopSeconds = 0
            }
            if (sleepStopSeconds > MINUTE_3) {
                isSleepState_NonMoving = false
            }
        }

        if (sleepStartSeconds_AVRHR <= MINUTE_10) {
            sleepStartSeconds_AVRHR += SECOND_FACTOR
            prevAVRHR_Array.add(heartRate)
        } else {
            if (prevAVRHR <= 0) {
                prevAVRHR = prevAVRHR_Array.average()
            } else {
                currAVRHR = prevAVRHR_Array.average()
                if (prevAVRHR > currAVRHR * 0.10) { //이전 평균에 비해 10%떨어질시
                    isSleepState_AVRHR = true
                } else {
                    prevAVRHR = currAVRHR
                    isSleepState_AVRHR = false
                }
            }
            sleepStartSeconds_AVRHR = 0
            prevAVRHR_Array.clear()
        }

        if (isSleepState_NonMoving || isSleepState_AVRHR) {//참일경우 수면시간 카운트 분기
            totalSleepSeconds += SECOND_FACTOR
            if (currentStatus > NON_THRESHOLD && currentStatus < 4) {
                realSleepSeconds += SECOND_FACTOR
            }
        }
    }

    private fun calculateMoving(
        etcSensorInfo: EtcSensorInfo,
        movPowerThreshold: Int,
        motionThreshold: Int
    ) {
        val turnValue = 2

        val adcPower = etcSensorInfo.adcPower
        val motionPower = etcSensorInfo.motionPower

        if (adcPower > movPowerThreshold || motionPower > motionThreshold) {
            movingTurn += turnValue
            if (!isMoving) {
                movingCount++
                isMoving = true
            }
        } else {
            isMoving = false
        }
    }

    private fun calculateRdi(
        vitalSensorInfo: VitalSensorInfo,
        brPThr: Int,
        hrPThr: Int
    ) {
        val isSleepState_NonMoving =
            currentStatus > NON_THRESHOLD && currentStatus < 4 //NON_THRESOHOLD = 0.4

        val etcSensorInfo = lastEtcSensorInfo ?: return

        val brP = etcSensorInfo.RRPower
        val RR = vitalSensorInfo.respirationRate
        val hrP = etcSensorInfo.HRPower

        if (!isSleepState_NonMoving) {
            brinValidCount = 0
            return
        }

        if (RR == 0 || ((brP < brPThr || RR < 6) && hrP > hrPThr)) {
            brinValidCount += SECOND_FACTOR
        } else if (
            (hrP > hrPThr && hrP < hrPThr * 6) && brP > brPThr * 30 && isSleepState_NonMoving
        ) {
            brinValidCount += SECOND_FACTOR //SECOND_FACTOR = 2
        } else {
            brinValidCount = 0
        }

        if (brinValidCount >= SECOND_FACTOR * 10) {
            rdiCount += 1
        }
    }
}

