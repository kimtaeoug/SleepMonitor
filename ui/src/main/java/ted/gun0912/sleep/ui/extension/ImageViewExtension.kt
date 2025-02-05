package ted.gun0912.sleep.ui.extension

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun ImageView.load(imageUrl: String) = suspendCoroutine<Unit> { continuation ->
    if (transitionName == imageUrl) {
        return@suspendCoroutine
    }
    transitionName = imageUrl
    Glide.with(this)
        .load(imageUrl)
        .addListener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                continuation.resume(Unit)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable?>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                continuation.resume(Unit)
                return false
            }
        })
        .into(this)
}


@BindingAdapter(
    value = ["loadImage"],
    requireAll = false
)
fun ImageView.loadImage(imageUrl: String?) {
    imageUrl ?: return
    Glide.with(this)
        .load(imageUrl)
        .into(this)
}

@BindingAdapter(value = ["drawableResId"])
fun ImageView.setDrawableResId(@DrawableRes resId: Int) {
    setImageResource(resId)
}
