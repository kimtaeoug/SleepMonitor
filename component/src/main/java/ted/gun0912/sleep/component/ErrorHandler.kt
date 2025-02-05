package ted.gun0912.sleep.component

interface ErrorHandler {

    fun logException(throwable: Throwable)

    fun handleError(throwable: Throwable)
}
