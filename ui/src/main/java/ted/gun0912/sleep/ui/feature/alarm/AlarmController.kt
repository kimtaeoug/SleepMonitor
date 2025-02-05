package ted.gun0912.sleep.ui.feature.alarm

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Vibrator
import kotlin.math.max


object AlarmController {

    private val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var audioManager: AudioManager? = null
    private const val STREAM_TYPE = AudioManager.STREAM_ALARM
    private var userVolume = 0

    fun start(context: Context) {
        val audioManager = (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).also {
            this.audioManager = it
        }
        userVolume = audioManager.getStreamVolume(STREAM_TYPE)

        //todo
        //알람 소리제거
//        RingtoneManager.getRingtone(context, alarmUri).apply {
//            val audioAttributes =
//                AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_ALARM)
//                    .build()
//            setAudioAttributes(audioAttributes)
//            play()
//        }.also { this.ringtone = it }
//
//        val alarmVolume = (audioManager.getStreamMaxVolume(STREAM_TYPE) * 0.8).toInt()
//        setStreamVolume(max(userVolume, alarmVolume))
//
//        val vibrator = (context.getSystemService(VIBRATOR_SERVICE) as Vibrator).also {
//            this.vibrator = it
//        }
//        val pattern = longArrayOf(0, 100, 1000, 300, 200, 100, 500, 200, 100)
//        vibrator.vibrate(pattern, 0)
    }

    fun stop() {
        setStreamVolume(userVolume)
        ringtone?.stop()
        ringtone = null

        vibrator?.cancel()
        vibrator = null
    }

    private fun setStreamVolume(volume: Int) {
        audioManager?.setStreamVolume(STREAM_TYPE, volume, AudioManager.FLAG_PLAY_SOUND)
    }
}
