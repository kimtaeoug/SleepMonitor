package ted.gun0912.sleep.common.exception

fun interface ErrorToasterListener {
    fun showErrorToast(exceptionType: ExceptionType)
}
