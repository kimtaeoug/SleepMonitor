package ted.gun0912.sleep.ui.extension

import android.os.Bundle
import ted.gun0912.sleep.common.exception.ErrorUtil

@Suppress("IMPLICIT_CAST_TO_ANY")
inline fun <reified T : Any> Bundle.getValue(key: String, defaultValue: T? = null): T =
    getNullableValue(key)
        ?: defaultValue
        ?: throw IllegalArgumentException("Bundle.get($key)에 값이 없음")

inline fun <reified T : Any> Bundle.getNullableValue(key: String): T? {
    val value = get(key) ?: return null
    return value as? T ?: run {
        ErrorUtil.handleUnExpectedCaseException { "${value::class.javaObjectType}가 ${T::class.javaObjectType} 타입이 아님" }
        null
    }
}

@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
inline fun <reified T : Any> Bundle.getValues(key: String): List<T> {
    val result = when (val value = get(key)) {
        null -> {
            ErrorUtil.handleUnExpectedCaseException { "Bundle.get($key)에 값이 없음" }
            emptyList()
        }
        is Iterable<*> -> value.toList()
        else -> {
            ErrorUtil.handleUnExpectedCaseException { "${value::class.javaObjectType}가 Iterable 타입이 아님" }
            emptyList()
        }
    }
    return result as? List<T> ?: run {
        ErrorUtil.handleUnExpectedCaseException { "${result::class.javaObjectType}가 ${T::class.javaObjectType} 타입이 아님" }
        emptyList()
    }
}

