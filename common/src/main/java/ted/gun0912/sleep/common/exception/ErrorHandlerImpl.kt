package ted.gun0912.sleep.common.exception

import ted.gun0912.sleep.common.extension.log
import ted.gun0912.sleep.component.ErrorHandler
import ted.gun0912.sleep.component.Toaster
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import javax.inject.Inject

class ErrorHandlerImpl @Inject constructor(
    private val toaster: Toaster,
) : ErrorHandler {

    override fun logException(throwable: Throwable) {
        if (canIgnoreThrowable(throwable)) {
            return
        }
        throwable.log()
    }

    override fun handleError(throwable: Throwable) {
        throwable.log()
        try {
            val exceptionType = getExceptionType(throwable)
            when (getExceptionType(throwable)) {
                ExceptionType.NETWORK -> {

                }
                ExceptionType.IGNORE -> {
                    // no-op
                }
                ExceptionType.NORMAL -> {
                    logException(throwable)
                    showErrorToast(exceptionType)
                }
            }

        } catch (e: Exception) {
            logException(RuntimeException("ErrorUtil::handleError 내부에러: ", e))
        }
    }

    private fun getExceptionType(throwable: Throwable): ExceptionType =
        when (throwable) {
            is UnknownHostException -> {
                ExceptionType.NETWORK
            }
            is SocketTimeoutException,
            is CancellationException -> {
                ExceptionType.IGNORE
            }
            else -> {
                ExceptionType.NORMAL
            }
        }

    private fun canIgnoreThrowable(throwable: Throwable): Boolean =
        when (throwable) {
            is ConnectException,
            is SocketTimeoutException,
            is CancellationException,
            is UnknownHostException -> true
            else -> false
        }


    private fun showErrorToast(exceptionType: ExceptionType) =
        when (exceptionType) {
            ExceptionType.IGNORE -> {
                // no-op
            }
            ExceptionType.NETWORK ->
                toaster.showErrorToast("네트워크 상태가 원활하지 않습니다.\n인터넷 연결상태를 확인 후 다시 시도해주세요")

            ExceptionType.NORMAL -> {
                //toaster.showErrorToast("오류가 발생했습니다.\n계속되면 채팅상담으로 꼭 알려주세요!") //YJH 0516
            }
        }
}
