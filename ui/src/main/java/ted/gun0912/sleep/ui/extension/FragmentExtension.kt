package ted.gun0912.sleep.ui.extension

import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Fragment.findListener(): T? =
    (targetFragment as? T)
        ?: (parentFragment as? T)
        ?: (activity as? T)


inline fun <reified T> Fragment.argumentValue(key: String, defaultValue: T? = null): Lazy<T> =
    lazy { getArgumentValue(key, defaultValue) }

inline fun <reified T> Fragment.argumentNullableValue(key: String): Lazy<T?> =
    lazy { getArgumentNullableValue(key) }


inline fun <reified T> Fragment.getArgumentValue(key: String, defaultValue: T? = null): T {
    val targetArguments = arguments ?: throw IllegalArgumentException("arguments 가 null일 수 없음")
    return targetArguments.getValue(key, defaultValue)
}

inline fun <reified T> Fragment.getArgumentNullableValue(key: String): T? {
    val targetArguments = arguments ?: throw IllegalArgumentException("arguments 가 null일 수 없음")
    return targetArguments.getNullableValue(key)
}

inline fun <reified T> Fragment.getArgumentValues(key: String): List<T> {
    val targetArguments = arguments ?: throw IllegalArgumentException("arguments 가 null일 수 없음")
    return targetArguments.getValues(key)
}

fun Fragment.getColorInt(@ColorRes colorResId: Int): Int =
    resources.getColor(colorResId, requireContext().theme)
