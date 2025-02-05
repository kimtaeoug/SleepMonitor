package ted.gun0912.sleep.ui.extension

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow

fun <T> SavedStateHandle.getNotNull(key: String): T =
    get<T>(key) ?: throw IllegalArgumentException("SavedStateHandle[$key]에 값이 존재하지 않음")

fun <T> SavedStateHandle.getOrDefault(key: String, defaultValue: T): T = get<T>(key) ?: defaultValue

fun <T> SavedStateHandle.getStateFlow(key: String): MutableStateFlow<T> =
    MutableStateFlow(getNotNull(key))

fun <T> SavedStateHandle.getStateFlow(key: String, defaultValue: T): MutableStateFlow<T> =
    MutableStateFlow(getOrDefault(key, defaultValue))
