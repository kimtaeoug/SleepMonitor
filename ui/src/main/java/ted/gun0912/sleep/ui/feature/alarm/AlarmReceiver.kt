package ted.gun0912.sleep.ui.feature.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.notification.NotificationInfo
import ted.gun0912.sleep.notification.createNotification
import java.time.LocalTime

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        logd("[AlarmReceiver onReceive]")
        val alarmTime: LocalTime = intent.getSerializableExtra(KEY_ALARM_TIME) as LocalTime
        logd("[AlarmReceiver onReceive]alarmTime: $alarmTime")

        AlarmController.start(context)
        val alarmIntent = AlarmActivity.getIntent(context, alarmTime).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(alarmIntent)

        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        val notification = context.createNotification(NOTIFICATION_INFO)
            .apply {
                contentIntent = PendingIntent.getActivity(
                    context,
                    NOTIFICATION_INFO.notificationId,
                    alarmIntent,
                    flags,
                )
            }
        val notificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_INFO.notificationId, notification)
    }


    companion object {
        private val NOTIFICATION_INFO = NotificationInfo.ALARM
        const val KEY_ALARM_TIME = "KEY_ALARM_TIME"
    }
}
