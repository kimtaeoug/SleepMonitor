package ted.gun0912.sleep.model.extension

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

private val zoneId = ZoneId.systemDefault()

object DateConverter {

    fun secondToLocalDateTime(second: Long): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochSecond(second), zoneId)

    fun secondToLocalDate(second: Long): LocalDate =
        secondToLocalDateTime(second).toLocalDate()
}

fun LocalDate.toSecond(): Long = atStartOfDay().toSecond()

fun LocalDateTime.toSecond(): Long =
    atZone(zoneId).toInstant().epochSecond
