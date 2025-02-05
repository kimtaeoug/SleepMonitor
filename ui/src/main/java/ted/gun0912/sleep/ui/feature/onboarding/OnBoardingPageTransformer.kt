package ted.gun0912.sleep.ui.feature.onboarding

import android.graphics.drawable.LayerDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class OnBoardingPageTransformer(private val layerBackground: LayerDrawable) :
    ViewPager2.PageTransformer {
    init {
        val size = layerBackground.numberOfLayers
        for (index in 0 until size) {
            val drawable = layerBackground.getDrawable(index)
            drawable.alpha = if (index == size - 1) 255 else 0
        }
    }

    override fun transformPage(view: View, position: Float) {

        val absPosition = abs(position)
        val layoutPosition = (view.parent as RecyclerView).getChildLayoutPosition(view)
        val currentDrawable = layerBackground.getDrawable(layoutPosition)
        when {
            position <= -1 || position >= 1 -> currentDrawable.alpha = 0
            position == 0f -> currentDrawable.alpha = 255
            else -> currentDrawable.alpha = (255 * (1 - absPosition)).toInt()
        }
    }
}
