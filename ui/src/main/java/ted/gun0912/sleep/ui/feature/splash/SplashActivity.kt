package ted.gun0912.sleep.ui.feature.splash

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels

import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.sleep.notification.NotificationInfo
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.ActivityTransitionType
import ted.gun0912.sleep.ui.base.BaseActivity
import ted.gun0912.sleep.ui.databinding.ActivitySplashBinding
import ted.gun0912.sleep.ui.feature.main.MainActivity
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingActivity
import ted.gun0912.sleep.ui.feature.splash.SplashViewModel.Event

@AndroidEntryPoint
class SplashActivity :
    BaseActivity<ActivitySplashBinding, SplashViewModel, Event>(R.layout.activity_splash) {
    override val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTransition = ActivityTransitionType.NONE
        cancelNotification()
    }

    private fun cancelNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NotificationInfo.SLEEP_TIME.notificationId)
    }

    override fun handleEvent(event: Event) = when (event) {
        is Event.ShowMain -> startMainActivity(event.sleepDeviceAddress, event.envDeviceAddress)
        Event.ShowOnboarding -> startOnboardingActivity()
    }

    private fun startMainActivity(sleepDeviceAddress: String, envDeviceAddress: String) {
        MainActivity.startActivity(this, sleepDeviceAddress, envDeviceAddress)
        finish()
    }

    private fun startOnboardingActivity() {
        OnBoardingActivity.startActivity(this)
        finish()
    }
}
