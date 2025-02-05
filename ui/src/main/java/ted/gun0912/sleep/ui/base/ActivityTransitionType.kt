package ted.gun0912.sleep.ui.base

import android.os.Parcelable
import androidx.annotation.AnimRes
import kotlinx.android.parcel.Parcelize
import ted.gun0912.sleep.ui.R

@Parcelize
enum class ActivityTransitionType(
    @AnimRes val showFrontAnimation: Int,
    @AnimRes val hideBackAnimation: Int,
    @AnimRes val returnBackAnimation: Int,
    @AnimRes val finishFrontAnimation: Int,
) : Parcelable {
    NONE(
        0,
        0,
        0,
        0
    ),
    FADE(
        android.R.anim.fade_in,
        android.R.anim.fade_out,
        android.R.anim.fade_in,
        android.R.anim.fade_out
    ),
    RIGHT_TO_LEFT(
        R.anim.slide_right_to_left,
        R.anim.slide_right_to_left_to_outside_10,
        R.anim.slide_left_to_right_from_outside_10,
        R.anim.slide_left_to_right
    ),
    BOTTOM_TO_TOP(
        R.anim.slide_from_bottom,
        R.anim.slide_up_to_outside_5,
        R.anim.slide_down_from_outside_5,
        R.anim.slide_to_bottom
    ),

    TOP_TO_BOTTOM(
        R.anim.slide_from_top,
        R.anim.slide_up_to_outside_5,
        R.anim.slide_down_from_outside_5,
        R.anim.slide_to_top
    ),
}
