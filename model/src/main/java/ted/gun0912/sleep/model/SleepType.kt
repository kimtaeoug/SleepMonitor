package ted.gun0912.sleep.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
enum class SleepType {
    DAY, NIGHT;

    companion object {
        fun fromLocalDateTime(dateTime: LocalDateTime): SleepType {
            return fromHour(dateTime.hour)
        }

        fun fromHour(hour: Int): SleepType {
            return if (hour in 9..16) DAY else NIGHT
        }
    }
}
