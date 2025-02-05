package ted.gun0912.sleep.design.extension

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import kotlin.math.roundToInt

fun View.setSelectableForeground() {
    val outValue = TypedValue().apply {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    }
    foreground = AppCompatResources.getDrawable(context, outValue.resourceId)
}

@BindingAdapter(value = ["android:layout_width"])
fun View.setWidth(width: Float) {
    layoutParams = (layoutParams as ViewGroup.LayoutParams).also {
        it.width = width.roundToInt()
    }
}

@BindingAdapter(value = ["android:layout_height"])
fun View.setHeight(height: Float) {
    layoutParams = (layoutParams as ViewGroup.LayoutParams).also {
        it.height = height.roundToInt()
    }
}

@BindingAdapter(value = ["android:layout_marginStart"])
fun View.setStartMargin(startMargin: Float) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        marginStart = startMargin.roundToInt()
    }
}

@BindingAdapter(value = ["android:layout_marginTop"])
fun View.setTopMargin(topMargin: Float) {
    this.layoutParams = (this.layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(leftMargin, topMargin.roundToInt(), rightMargin, bottomMargin)
    }
}

@BindingAdapter(value = ["android:layout_marginEnd"])
fun View.setEndMargin(endMargin: Float) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        marginEnd = endMargin.roundToInt()
    }
}

@BindingAdapter(value = ["android:layout_marginBottom"])
fun View.setBottomMargin(bottomMargin: Float) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(leftMargin, topMargin, rightMargin, bottomMargin.roundToInt())
    }
}

@BindingAdapter(value = ["android:paddingHorizontal"])
fun View.setPaddingHorizontal(horizontalPadding: Float) {
    layoutParams = (layoutParams as ViewGroup.LayoutParams).apply {
        setPadding(
            horizontalPadding.roundToInt(),
            paddingTop,
            horizontalPadding.roundToInt(),
            paddingBottom
        )
    }
}

@BindingAdapter(value = ["android:paddingVertical"])
fun View.setPaddingVertical(verticalPadding: Float) {
    layoutParams = (layoutParams as ViewGroup.LayoutParams).apply {
        setPadding(
            paddingStart,
            verticalPadding.roundToInt(),
            paddingEnd,
            verticalPadding.roundToInt()
        )
    }
}

@BindingAdapter(value = ["id"])
fun View.setCustomId(@IdRes viewIdResId: Int) {
    id = viewIdResId
}

@BindingAdapter(value = ["selected"])
fun View.setSelectedBinding(selected: Boolean) {
    isSelected = selected
}

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(windowToken, 0)
}

@BindingAdapter("backgroundResId")
fun View.setBackgroundResId(@DrawableRes value: Int?) {
    value ?: return
    setBackgroundResource(value)
}

@BindingAdapter("android:visibility")
fun View.setVisibility(visible: Boolean) {
    isVisible = visible
}
