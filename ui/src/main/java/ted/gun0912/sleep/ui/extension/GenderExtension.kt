package ted.gun0912.sleep.ui.extension

import ted.gun0912.sleep.model.Gender
import ted.gun0912.sleep.ui.R


val Gender.textResId: Int
    get() = when (this) {
        Gender.MAN -> R.string.man
        Gender.WOMAN -> R.string.woman
    }
