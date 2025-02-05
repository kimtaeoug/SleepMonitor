package ted.gun0912.sleep.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ted.gun0912.sleep.ui.R

abstract class BaseBottomDialogFragment<B : ViewDataBinding, VM : BaseViewModel<VE>, VE : ViewEvent>(
    @LayoutRes private val layoutResId: Int
) : BaseDialogFragment<B, VM, VE>(layoutResId) {

    protected val bottomSheetBehavior: BottomSheetBehavior<*>?
        get() = (dialog as? BottomSheetDialog)?.behavior

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme_Round

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        BottomSheetDialog(requireContext(), theme)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }
}
