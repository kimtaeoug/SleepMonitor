package ted.gun0912.sleep.notification

import android.app.NotificationManager
import androidx.annotation.StringRes


enum class NotificationInfo(
    @StringRes val channelIdResId: Int,
    @StringRes val channelNameResId: Int,
    @StringRes val channelDescriptionResId: Int,
    val importance: Int,
    val showBadge: Boolean,
    val notificationId: Int,
    @StringRes val notificationTitleResId: Int,
    @StringRes val notificationContentResId: Int,
    val autoCancel: Boolean,
    val canDismiss: Boolean,
) {

    BLUETOOTH(
        R.string.bluetooth_channel_name,
        R.string.bluetooth_channel_name,
        R.string.bluetooth_channel_description,
        NotificationManager.IMPORTANCE_LOW,
        false,
        200,
        R.string.bluetooth_notification_title,
        R.string.bluetooth_notification_content,
        false,
        false,
    ),
    SLEEP_TIME(
        R.string.sleep_time_channel_name,
        R.string.sleep_time_channel_name,
        R.string.sleep_time_channel_description,
        NotificationManager.IMPORTANCE_HIGH,
        true,
        300,
        R.string.sleep_time_notification_title,
        R.string.sleep_time_notification_content,
        true,
        true,
    ),
    ALARM(
        R.string.alarm_channel_name,
        R.string.alarm_channel_name,
        R.string.alarm_channel_description,
        NotificationManager.IMPORTANCE_HIGH,
        true,
        400,
        R.string.alarm_notification_title,
        R.string.alarm_notification_content,
        false,
        false,
    ),
    ;

}
