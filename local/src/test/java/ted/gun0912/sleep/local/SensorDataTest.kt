package ted.gun0912.sleep.local

import org.junit.Test
import ted.gun0912.sleep.local.util.DailySleepStatisticUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SensorDataTest {

    @Test
    fun test1() {
        calculate("202209032215.txt")
    }

    @Test
    fun test2() {
        calculate("202209052329.txt")
    }

    private fun calculate(fileName: String) {
        val rawText = getRawText(fileName)
        val dateText = fileName.replace(".txt", "")
        val sleepTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")

        val sleepTime = LocalDateTime.parse(dateText, sleepTimeFormatter)
        val wakeUpTime = sleepTime.plusHours(8)
        val result = DailySleepStatisticUtil.getDailySleepStatistic(
            36,
            sleepTime.toLocalDate(),
            sleepTime,
            wakeUpTime,
            rawText,
        )
        println(result)
    }


    private fun getRawText(fileName: String): String {
        val inputStream = javaClass.classLoader.getResourceAsStream(fileName)
        return inputStream.bufferedReader().use { it.readText() }
    }
}
