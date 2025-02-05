package ted.gun0912.sleep.device.impl

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.design.databinding.ViewToastBinding
import ted.gun0912.sleep.component.Toaster
import javax.inject.Inject

internal class ToasterImpl @Inject constructor(private val application: Application) : Toaster {

    private var previousToast: Toast? = null
    private var previousSnackBar: Snackbar? = null

    private var visibleActivity: Activity? = null

    init {
        logd("ToasterImpl init")
        setupActivityLifeCycle()
    }

    private fun setupActivityLifeCycle() {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                visibleActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                visibleActivity = null
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    override fun showSuccessToast(text: CharSequence) {
        showToast(text, true, Toast.LENGTH_LONG)
    }

    override fun showErrorToast(text: CharSequence) {
        showToast(text, false, Toast.LENGTH_LONG)
    }

    override fun showToast(text: CharSequence, isSuccess: Boolean) {
        showToast(text, isSuccess, Toast.LENGTH_LONG)
    }

    private fun showToast(text: CharSequence, isSuccess: Boolean, duration: Int, yOffset: Int = 0) {
        if (visibleActivity == null) {
            return
        }
        Handler(Looper.getMainLooper()).post {

            val replacedMessage = if (text.length > 200) {
                text.subSequence(0, 200)
            } else {
                text
            }

            if (canShowToast()) {
                showToastInternal(replacedMessage, isSuccess, duration, yOffset)
            } else {
                showSnackBar(replacedMessage, duration)
            }

        }
    }

    private fun canShowToast(): Boolean =
        NotificationManagerCompat.from(application).areNotificationsEnabled()
            || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private fun showToastInternal(
        text: CharSequence,
        isSuccess: Boolean,
        duration: Int,
        yOffset: Int = 0,
    ) {
        previousToast?.cancel()
        val context = application
        val toast = Toast(context)
            .apply {
                setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, yOffset)
                setDuration(duration)
                view = with(ViewToastBinding.inflate(LayoutInflater.from(context))) {
                    this.text = text
                    this.isSuccess = isSuccess
                    root
                }
            }
        toast.show()
        previousToast = toast
    }

    private fun showSnackBar(text: CharSequence, toastDuration: Int) {
        previousSnackBar?.dismiss()

        val rootView = getRootView() ?: return
        val snackBarDuration =
            if (toastDuration == Toast.LENGTH_LONG) {
                Snackbar.LENGTH_LONG
            } else {
                Snackbar.LENGTH_SHORT
            }
        val snackBar = Snackbar.make(rootView, text, snackBarDuration)
        snackBar.show()
        previousSnackBar = snackBar
    }

    private fun getRootView(): View? {

        val activity = visibleActivity ?: return null
        val contentViewGroup: ViewGroup? = activity.findViewById(android.R.id.content)
        return contentViewGroup?.getChildAt(0) ?: activity.window.decorView.rootView
    }

}
