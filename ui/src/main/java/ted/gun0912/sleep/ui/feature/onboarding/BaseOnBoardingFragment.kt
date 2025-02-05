package ted.gun0912.sleep.ui.feature.onboarding

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import ted.gun0912.sleep.common.util.logd
import ted.gun0912.sleep.ui.base.BaseFragment
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.extension.findListener

abstract class BaseOnBoardingFragment<B : ViewDataBinding, VM : BaseViewModel<VE>, VE : ViewEvent>(
    @LayoutRes private val layoutResId: Int,
    private val step: OnBoardingStep,
) : BaseFragment<B, VM, VE>(layoutResId) {

    protected fun showNextStep() {
        findListener<OnBoardingListener>()?.showNextStep(step)
    }
}
