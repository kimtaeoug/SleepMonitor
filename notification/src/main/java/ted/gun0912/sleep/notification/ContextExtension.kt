package ted.gun0912.sleep.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService

fun Context.createNotification(notificationInfo: NotificationInfo): Notification {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(notificationInfo)
    }
    val flags =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, flags)


    return NotificationCompat.Builder(this, getString(notificationInfo.channelIdResId))
        .setContentTitle(getString(notificationInfo.notificationTitleResId))
        .setContentText(getString(notificationInfo.notificationContentResId))
        .setSmallIcon(ted.gun0912.sleep.design.R.drawable.ic_launcher_foreground)
        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
        .setAutoCancel(notificationInfo.autoCancel)
        .setContentIntent(pendingIntent)
        .setOngoing(!notificationInfo.canDismiss)
        .build()
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.createNotificationChannel(notificationInfo: NotificationInfo) {

    val channel = NotificationChannel(
        getString(notificationInfo.channelIdResId),
        getString(notificationInfo.channelNameResId),
        notificationInfo.importance,
    ).apply {
        description = getString(notificationInfo.channelDescriptionResId)
        setShowBadge(notificationInfo.showBadge)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }
    val notificationManager =
        getSystemService(LifecycleService.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}
