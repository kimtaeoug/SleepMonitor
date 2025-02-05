package ted.gun0912.sleep.ui.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance

object CorBus {

    private val mutableSharedFlow = MutableSharedFlow<CorEvent>()
    val sharedFlow get() = mutableSharedFlow.asSharedFlow()

    suspend fun emit(item: CorEvent) {
        mutableSharedFlow.emit(item)
    }

    suspend inline fun <reified T : CorEvent> collect(crossinline onCollect: suspend (T) -> Unit) {
        sharedFlow.filterIsInstance<T>()
            .collect { onCollect(it) }
    }
}
