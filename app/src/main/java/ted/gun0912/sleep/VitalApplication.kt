package ted.gun0912.sleep

import androidx.annotation.CallSuper
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ted.gun0912.sleep.common.exception.ErrorHandlerImpl
import ted.gun0912.sleep.common.exception.ErrorUtil
import ted.gun0912.sleep.component.Device
import ted.gun0912.sleep.component.Toaster
import javax.inject.Inject

@HiltAndroidApp
class VitalApplication : MultiDexApplication(), Configuration.Provider {

    @Inject
    lateinit var device: Device

    @Inject
    lateinit var toaster: Toaster

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        instance = this
        setupError()
        val userId = device.deviceId
    }

    private fun setupError() {
        ErrorUtil.init(ErrorHandlerImpl(toaster))
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.INFO)
            .build()

    companion object {
        lateinit var instance: VitalApplication
            private set

    }
}
