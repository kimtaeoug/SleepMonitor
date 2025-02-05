package ted.gun0912.sleep.ui.feature.main

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import ted.gun0912.sleep.ui.base.BaseFragment
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent

abstract class BaseTabFragment<B : ViewDataBinding, VM : BaseViewModel<VE>, VE : ViewEvent>(@LayoutRes layoutResId: Int) :
    BaseFragment<B, VM, VE>(layoutResId) {

}
