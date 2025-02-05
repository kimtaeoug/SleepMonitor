package ted.gun0912.sleep.device.impl

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import ted.gun0912.sleep.common.exception.ErrorUtil
import ted.gun0912.sleep.component.Device
import javax.inject.Inject

internal class DeviceImpl @Inject constructor(private val application: Application) : Device {

    override val deviceId: String
        @SuppressLint("HardwareIds")
        get() = runCatching {
            val contentResolver = application.contentResolver
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        }
            .onFailure { ErrorUtil.logException(it) }
            .getOrDefault("알 수 없음")
}
