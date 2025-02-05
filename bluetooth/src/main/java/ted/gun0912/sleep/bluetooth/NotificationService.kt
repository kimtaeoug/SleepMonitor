package ted.gun0912.sleep.bluetooth

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LifecycleService
import ted.gun0912.sleep.notification.NotificationInfo
import ted.gun0912.sleep.notification.createNotification

abstract class NotificationService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        startForegroundService()
        return result
    }

    override fun onDestroy() {
        // when user has disconnected from the sensor, we have to cancel the notification that we've created some milliseconds before using unbindService
        cancelNotification()
        stopForegroundService()
        super.onDestroy()
    }

    /**
     * Sets the service as a foreground service
     */
    private fun startForegroundService() {
        // when the activity closes we need to show the notification that user is connected to the peripheral sensor
        // We start the service as a foreground service as Android 8.0 (Oreo) onwards kills any running background services
        val notification = createNotification(notificationInfo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(notificationInfo.notificationId, notification)
        } else {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notificationInfo.notificationId, notification)
        }
    }

    /**
     * Stops the service as a foreground service
     */
    private fun stopForegroundService() {
        // when the activity rebinds to the service, remove the notification and stop the foreground service
        // on devices running Android 8.0 (Oreo) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        } else {
            cancelNotification()
        }
    }

    /**
     * Cancels the existing notification. If there is no active notification this method does nothing
     */
    private fun cancelNotification() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(notificationInfo.notificationId)
    }

    companion object {
        private val notificationInfo = NotificationInfo.BLUETOOTH
    }
}
