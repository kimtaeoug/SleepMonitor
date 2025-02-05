package ted.gun0912.sleep.local.util

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round

internal object SleepScoreUtil {

    fun getSleepScore(
        age: Int,
        realSleepSeconds: Int,
        sleepEfficiency: Float,
        movingIndex: Float
    ): Int {
        val sleepIndex = (getSleepIndex(realSleepSeconds, age)).coerceAtLeast(10F)

        var movRating = 100 - (movingIndex * 2)

        if (movRating < 40) {
            movRating = 40F
        }
        return (movRating * 0.4 + sleepIndex * 0.3 + sleepEfficiency * 0.3).toInt()
    }

    fun getSleepScore2(realSleepSeconds: Int, age: Int): Int {
        val sleepIndex = getSleepIndex2(realSleepSeconds, age)

        return round(sleepIndex * 100).toInt()
    }

    private fun getSleepIndex(realSleepSeconds: Int, age: Int): Float {
        // 나이대별 적정 수면시간 범위
        val (minSleepHour, maxSleepHour) = getStandardSleepHourRange(age)

        val sleepHour = realSleepSeconds / 3600
        // 수면부족, 수면초과 여부에 따라서 그에 맞는 지수 계산
        // 해당되지 않으면 100점
        val sleepIndex = when {
            sleepHour > maxSleepHour -> getOverSleepScore(maxSleepHour, realSleepSeconds)
            sleepHour < minSleepHour -> getLackSleepScore(minSleepHour, realSleepSeconds)
            else -> 100f
        }
        return max(sleepIndex, 10f)
    }

    private fun getSleepIndex2(realSleepSeconds: Int, age: Int): Float {
        //나이대별 적정 수면시간 범위
        val ageSleepHour = getStandardSleepHourRange2(age)
        val sleepHour = realSleepSeconds / 3600f
        val sleepIndex = when {
            sleepHour > ageSleepHour -> getOverSleepScore2(ageSleepHour, sleepHour)
            sleepHour < ageSleepHour -> getLackSleepScore2(ageSleepHour, sleepHour)
            else -> 7
        }
        return sleepIndex.toFloat()
    }

    private fun getStandardSleepHourRange(age: Int): Pair<Int, Int> =
        when {
            age <= 13 -> 9 to 11 // 9 to 11의 중간값
            age <= 18 -> 8 to 10 // 8 to 10
            age <= 64 -> 7 to 9 // 7 to 9
            else -> 6 to 8 // 6 to 8
        }

    private fun getStandardSleepHourRange2(age: Int): Int =
        when {
            age <= 13 -> 10 // 9 to 11의 중간값
            age <= 18 -> 9 // 8 to 10
            age <= 64 -> 8 // 7 to 9
            else -> 7 // 6 to 8
        }

    private fun getLackSleepScore(minHour: Int, sleepSecond: Int): Float {
        val lackIndex = (minHour * 3600f - sleepSecond) / 1800
        return getSleepScoreByIndex(lackIndex)
    }

    private fun getLackSleepScore2(minHour: Int, sleepHour: Float): Float {

        return (minHour - (minHour - sleepHour)) / minHour
    }

    private fun getOverSleepScore(maxHour: Int, sleepSecond: Int): Float {
        val overIndex = (sleepSecond - maxHour * 3600f) / 1800

        return getSleepScoreByIndex(overIndex)
    }

    private fun getOverSleepScore2(maxHour: Int, sleepHour: Float): Float {

        return (maxHour - (sleepHour - maxHour)) / maxHour
    }

    private fun getSleepScoreByIndex(index: Float): Float = (100 - (2f.pow(index) / 2 * 2))
}
