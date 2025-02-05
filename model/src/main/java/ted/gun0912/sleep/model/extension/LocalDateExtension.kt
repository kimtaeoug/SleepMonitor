package ted.gun0912.sleep.model.extension

import java.time.Duration
import java.time.LocalDate

fun LocalDate.between(endLocalDate: LocalDate): Duration =
    Duration.between(this.atStartOfDay(), endLocalDate.atStartOfDay())
