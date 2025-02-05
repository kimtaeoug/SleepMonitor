package ted.gun0912.sleep.design.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import android.view.View
import android.view.WindowManager


inline val Int.dp: Int
    get() = toFloat().dp.toInt()

inline val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

inline val Context.screenWidth: Int
    get() = screenSize.x

inline val Context.screenHeight: Int
    get() = screenSize.y

inline val Context.screenSize: Point
    get() = Point().apply {
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(this)
    }
inline val View.screenWidth: Int
    get() = context.screenWidth

inline val View.screenHeight: Int
    get() = context.screenHeight
