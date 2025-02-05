package ted.gun0912.sleep.common.exception

import ted.gun0912.sleep.component.ErrorHandler

object ErrorUtil {
    private var handler: ErrorHandler? = null

    fun init(handler: ErrorHandler) {
        this.handler = handler
    }

    fun logException(throwable: Throwable) {
        handler?.logException(throwable)
    }

    fun logException(message: String) {
        logException(IllegalStateException(message))
    }

    fun handleError(throwable: Throwable) {
        handler?.handleError(throwable)
    }

    inline fun handleUnExpectedCaseException(message: () -> String) {
        handleError(IllegalArgumentException("[존재할 수 없는 case]${message()}"))
    }
}
