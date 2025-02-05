package ted.gun0912.sleep.device.impl

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import ted.gun0912.sleep.component.Haptic
import javax.inject.Inject

internal class HapticImpl @Inject constructor(application: Application) : Haptic {

    private val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    override fun success() = vibrate(Type.SUCCESS)
    override fun warning() = vibrate(Type.WARNING)
    override fun failure() = vibrate(Type.FAILURE)
    override fun interact() = vibrate(Type.INTERACT)

    private fun vibrate(type: Type) {
        vibrate(type.timings, type.amplitudes)
    }

    private fun vibrate(timings: LongArray, amplitudes: IntArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(timings, -1)
        }
    }

    enum class Type(val timings: LongArray, val amplitudes: IntArray) {
        SUCCESS(longArrayOf(0, 20, 80, 30), intArrayOf(0, 48, 0, 80)),
        WARNING(longArrayOf(0, 20, 40, 20), intArrayOf(0, 96, 0, 64)),
        FAILURE(
            longArrayOf(0, 20, 40, 20, 40, 30, 30, 60),
            intArrayOf(0, 64, 0, 64, 0, 120, 0, 64)
        ),
        INTERACT(longArrayOf(0, 10), intArrayOf(0, 72));
    }
}
