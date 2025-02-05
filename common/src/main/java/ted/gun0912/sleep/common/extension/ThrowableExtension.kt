package ted.gun0912.sleep.common.extension

import ted.gun0912.sleep.common.exception.ErrorUtil
import ted.gun0912.sleep.common.util.loge
import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.log() {
    val stringWriter = StringWriter()
    printStackTrace(PrintWriter(stringWriter))
    loge(stringWriter.toString())
}

fun Throwable.logException() {
    ErrorUtil.logException(this)
}

fun Throwable.handleError() {
    ErrorUtil.handleError(this)
}
