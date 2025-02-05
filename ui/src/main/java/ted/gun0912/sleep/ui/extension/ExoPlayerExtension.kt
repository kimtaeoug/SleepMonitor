package ted.gun0912.sleep.ui.extension

import androidx.annotation.StringRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

fun StyledPlayerView.play(lifecycle: Lifecycle, @StringRes videoPath: Int) {
    val videoPlayer = ExoPlayer.Builder(context).build().apply {
        volume = 0f
        repeatMode = Player.REPEAT_MODE_ALL
        playWhenReady = true
        setMediaItem(MediaItem.fromUri(context.getString(videoPath)))
        prepare()
    }

    player = videoPlayer
    useController = false
    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onDestroy(owner: LifecycleOwner) {
            videoPlayer.stop()
            videoPlayer.release()
        }
    })
}
