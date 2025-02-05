package ted.gun0912.sleep.common.util

import android.util.Log
import ted.gun0912.sleep.common.BuildConfig


private const val TAG = "ted"

private fun buildLogMsg(message: String): String {
    val ste = Thread.currentThread().stackTrace[4]
    val fileName = ste.fileName.replace(".java", "").replace(".kt", "")
    return "[$fileName::${ste.methodName} (${ste.fileName}:${ste.lineNumber})]$message"
}

private fun isNotDebugMode() = !BuildConfig.DEBUG

fun logv(paramString: String) {
    if (isNotDebugMode()) return
    Log.v(TAG, buildLogMsg(paramString))
}

fun logd(paramString: String) {
    if (isNotDebugMode()) return
    Log.d(TAG, buildLogMsg(paramString))
}

fun logi(paramString: String) {
    if (isNotDebugMode()) return
    Log.i(TAG, buildLogMsg(paramString))
}

fun logw(paramString: String) {
    if (isNotDebugMode()) return
    Log.w(TAG, buildLogMsg(paramString))
}

fun loge(paramString: String) {
    if (isNotDebugMode()) return
    Log.e(TAG, buildLogMsg(paramString))
}

fun loge(name: String, paramString: String) {
    if (isNotDebugMode()) return
    Log.e(TAG, buildLogMsg("$name: $paramString"))
}
