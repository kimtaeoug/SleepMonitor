package ted.gun0912.sleep.ui.eventbus

import java.time.LocalDate

sealed class CorEvent {

    suspend fun emit() {
        CorBus.emit(this)
    }

    object Alarm : CorEvent()
    data class NewRecord(val recordDay: LocalDate) : CorEvent()
}
