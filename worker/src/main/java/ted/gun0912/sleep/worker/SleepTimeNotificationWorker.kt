package ted.gun0912.sleep.worker

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.LifecycleService
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.notification.NotificationInfo
import ted.gun0912.sleep.notification.createNotification
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class SleepTimeNotificationWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val sleepNotificationTimeSecond = inputData.getInt(EXTRA_SLEEP_NOTIFICATION_TIME_SECOND, -1)
        val sleepNotificationTime = LocalTime.ofSecondOfDay(sleepNotificationTimeSecond.toLong())
        applicationContext.showNotification()

        enqueue(applicationContext, sleepNotificationTime)
        return Result.success()
    }

    private fun Context.showNotification() {
        val notification = createNotification(notificationInfo)
        val notificationManager =
            getSystemService(LifecycleService.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationInfo.notificationId, notification)
    }

    companion object {
        private val notificationInfo = NotificationInfo.SLEEP_TIME
        private const val TAG = "SLEEP_TIME_NOTIFICATION"
        private const val EXTRA_SLEEP_NOTIFICATION_TIME_SECOND =
            "EXTRA_SLEEP_NOTIFICATION_TIME_SECOND"

        fun enqueue(context: Context, sleepNotificationTime: LocalTime): Operation {
            cancel(context)
            logd("sleepNotificationTime: $sleepNotificationTime")
            val initialDelay = calculateInitialDelay(sleepNotificationTime)
            logd("initialDelay: $initialDelay")
            val workRequest = OneTimeWorkRequestBuilder<SleepTimeNotificationWorker>()
                .setInputData(workDataOf(EXTRA_SLEEP_NOTIFICATION_TIME_SECOND to sleepNotificationTime.toSecondOfDay()))
                .setInitialDelay(initialDelay.seconds, TimeUnit.SECONDS)
                .addTag(TAG)
                .build()
            return WorkManager.getInstance(context).enqueue(workRequest)
        }

        fun cancel(context: Context): Operation {
            return WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
        }

        private fun calculateInitialDelay(alarmTime: LocalTime): Duration {
            val adjustDays = if (LocalTime.now().isBefore(alarmTime)) {
                0
            } else {
                1
            }
            val now = LocalDateTime.now()
            val tomorrowAlarmTime = now.plusDays(adjustDays.toLong())
                .withHour(alarmTime.hour).withMinute(alarmTime.minute).withSecond(0)
            return Duration.between(now, tomorrowAlarmTime)
        }
    }
}
