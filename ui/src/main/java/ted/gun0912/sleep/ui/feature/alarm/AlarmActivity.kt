package ted.gun0912.sleep.ui.feature.alarm

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ted.gun0912.sleep.notification.NotificationInfo
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.ActivityTransitionType
import ted.gun0912.sleep.ui.base.BaseActivity
import ted.gun0912.sleep.ui.base.template.ActivityTemplate
import ted.gun0912.sleep.ui.databinding.ActivityAlarmBinding
import ted.gun0912.sleep.ui.eventbus.CorBus
import ted.gun0912.sleep.ui.eventbus.CorEvent
import ted.gun0912.sleep.ui.feature.alarm.AlarmViewModel.Event
import java.time.LocalTime


@AndroidEntryPoint
class AlarmActivity :
    BaseActivity<ActivityAlarmBinding, AlarmViewModel, Event>(
        R.layout.activity_alarm
    ) {
    override val viewModel: AlarmViewModel by viewModels()

    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTransition = ActivityTransitionType.NONE
        setupFinishButton()
        lifecycleScope.launch {
            CorBus.emit(CorEvent.Alarm)
        }
    }


    override fun handleEvent(event: Event) = Unit

    private fun setupFinishButton() {
        /*binding.progressBar.max = FINISH_LONG_CLICK_DURATION
        binding.viewFinish.setOnLongClickListener {
            object : CountDownTimer(FINISH_LONG_CLICK_DURATION.toLong(), 1) {
                override fun onTick(millisUntilFinished: Long) {
                    if (binding.viewFinish.isPressed) {
                        binding.progressBar.progress =
                            FINISH_LONG_CLICK_DURATION - millisUntilFinished.toInt()
                    } else {
                        binding.progressBar.progress = 0
                        cancel()
                    }
                }

                override fun onFinish() {
                    finish()
                }
            }.start()

            true
        }*/
        binding.viewFinish.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        AlarmController.stop()
        notificationManager.cancel(NOTIFICATION_INFO.notificationId)
        super.onDestroy()
    }

    override fun onBackPressed() {

    }


    companion object : ActivityTemplate<AlarmActivity>() {
        private const val FINISH_LONG_CLICK_DURATION = 2000

        private val NOTIFICATION_INFO = NotificationInfo.ALARM

        fun getIntent(context: Context, alarmTime: LocalTime): Intent =
            getIntent(context, AlarmViewModel.KEY_ALARM_TIME to alarmTime)

        fun startActivity(context: Context, alarmTime: LocalTime) {
            val intent = getIntent(context, alarmTime)
            context.startActivity(intent)
        }
    }
}
