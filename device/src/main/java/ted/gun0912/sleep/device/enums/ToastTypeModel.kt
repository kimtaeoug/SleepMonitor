package ted.gun0912.sleep.device.enums

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import ted.gun0912.sleep.design.R


internal enum class ToastTypeModel(
    @DrawableRes val imageResId: Int,
    @ColorRes val backgroundResId: Int,
) {
    SUCCESS(
        R.drawable.ic_check_24,
        R.color.toast_success
    ),
    ERROR(
        R.drawable.ic_error_24,
        R.color.toast_error
    )
}
