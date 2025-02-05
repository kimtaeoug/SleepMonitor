package ted.gun0912.sleep.ui.extension

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import ted.gun0912.sleep.common.util.loge
import java.io.Serializable


// https://github.com/Kotlin/anko/blob/78cfe0a8708709ee7cf0d4b9ffb3b21e3ed548ab/anko/library/static/commons/src/main/java/Internals.kt#L153
fun Intent.putExtra(key: String, value: Any?) {
    when (value) {
        null -> {
            // no-op
        }
        is Int -> putExtra(key, value)
        is Long -> putExtra(key, value)
        is CharSequence -> putExtra(key, value)
        is String -> putExtra(key, value)
        is Float -> putExtra(key, value)
        is Double -> putExtra(key, value)
        is Char -> putExtra(key, value)
        is Short -> putExtra(key, value)
        is Boolean -> putExtra(key, value)
        is Serializable -> putExtra(key, value)
        is Bundle -> putExtra(key, value)
        is Parcelable -> putExtra(key, value)
        is Array<*> -> when {
            value.isArrayOf<CharSequence>() -> putExtra(key, value)
            value.isArrayOf<String>() -> putExtra(key, value)
            value.isArrayOf<Parcelable>() -> putExtra(key, value)
            else -> loge("Intent extra $key has wrong type ${value.javaClass.name}")
        }
        is IntArray -> putExtra(key, value)
        is LongArray -> putExtra(key, value)
        is FloatArray -> putExtra(key, value)
        is DoubleArray -> putExtra(key, value)
        is CharArray -> putExtra(key, value)
        is ShortArray -> putExtra(key, value)
        is BooleanArray -> putExtra(key, value)
        else -> loge("Intent extra $key has wrong type ${value.javaClass.name}")
    }
}
