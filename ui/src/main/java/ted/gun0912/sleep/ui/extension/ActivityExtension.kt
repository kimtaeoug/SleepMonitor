package ted.gun0912.sleep.ui.extension

import android.app.Activity
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.setTransparentStatusBar() {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = resources.getColor(android.R.color.transparent, theme)

        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}

fun Activity.setAppearanceLightStatusBars(isLight: Boolean) {
    val rootView = findViewById<View?>(android.R.id.content)?.rootView
        ?: window.decorView.findViewById(android.R.id.content)
        ?: return
    val controller = WindowInsetsControllerCompat(window, rootView)
    controller.isAppearanceLightStatusBars = isLight
}

inline fun <reified T> Activity.intentValue(key: String, defaultValue: T? = null): Lazy<T> =
    lazy { getIntentValue(key, defaultValue) }

inline fun <reified T> Activity.intentNullableValue(key: String): Lazy<T?> =
    lazy { getIntentNullableValue(key) }


inline fun <reified T> Activity.getIntentValue(key: String, defaultValue: T? = null): T =
    intent.extras?.getValue(key, defaultValue)
        ?: defaultValue
        ?: throw IllegalArgumentException("intent.extras 가 null일 수 없음")

inline fun <reified T> Activity.getIntentNullableValue(key: String): T? =
    intent.extras?.getNullableValue(key)

inline fun <reified T> Activity.getIntentValues(key: String): List<T> =
    intent.extras?.getValues(key) ?: throw IllegalArgumentException("intent.extras 가 null일 수 없음")
