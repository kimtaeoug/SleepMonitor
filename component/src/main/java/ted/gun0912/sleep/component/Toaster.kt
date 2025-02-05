package ted.gun0912.sleep.component

interface Toaster {
    fun showSuccessToast(text: CharSequence)
    fun showErrorToast(text: CharSequence)
    fun showToast(text: CharSequence, isSuccess: Boolean)
}
