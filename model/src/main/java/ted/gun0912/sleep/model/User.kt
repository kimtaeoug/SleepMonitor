package ted.gun0912.sleep.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class User(
    val gender: Gender,
    val birthYear: Int,
    @Contextual
    val sleepTime: LocalTime,
    val needSleepTimeNotification: Boolean,
) {
    val age
        get() = LocalDate.now().year - birthYear
}
