package ted.gun0912.sleep.ui.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import java.util.concurrent.TimeUnit

fun <T> Flow<T>.onSuccessCompletion(
    action: suspend () -> Unit,
) =
    onCompletion { exception ->
        if (exception == null) {
            action.invoke()
        }
    }

fun ticker(
    delay: Number,
    initialDelay: Number = 0,
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
): Flow<Unit> = flow {
    val initialDelayMillis = timeUnit.toMillis(initialDelay.toLong())
    val delayMillis = timeUnit.toMillis(delay.toLong())

    kotlinx.coroutines.delay(initialDelayMillis)
    while (true) {
        emit(Unit)
        kotlinx.coroutines.delay(delayMillis)
    }
}
