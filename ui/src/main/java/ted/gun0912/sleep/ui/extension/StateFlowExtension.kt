package ted.gun0912.sleep.ui.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update

fun <T> StateFlow<T?>.observe(lifecycleOwner: LifecycleOwner, action: (value: T) -> Unit) =
    lifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
        collect { action(it ?: return@collect) }
    }

inline val <reified T> StateFlow<T?>.requireValue: T
    get() = value ?: throw IllegalArgumentException("StateFlow value is null: $this")

fun StateFlow<String?>.isEmpty(): Boolean = value.isNullOrEmpty()

fun StateFlow<String?>.isNotEmpty(): Boolean = !isEmpty()

fun StateFlow<Boolean?>.isTrue(): Boolean = value == true

fun StateFlow<Boolean?>.isFalse(): Boolean = value == false

fun <T> MutableStateFlow<MutableList<T>>.updateList(apply: MutableList<T>.() -> Unit = {}) {
    value = value.toMutableList().apply(apply)
}

fun <T> MutableStateFlow<T?>.update(apply: T.() -> Unit = {}) {
    val before = value ?: return
    val after = value?.apply(apply)
    if (before == after) {
        value = null
    }
    value = after
}


/**
 * val flow = MutableStateFlow<List<Int>>(listOf(1, 2, 3)) // 1, 2, 3
 * flow.updateMap { value -> value + 1 } // 2, 3, 4
 */
inline fun <T> MutableStateFlow<List<T>>.updateMap(function: (T) -> T) =
    update { it.map(function) }
