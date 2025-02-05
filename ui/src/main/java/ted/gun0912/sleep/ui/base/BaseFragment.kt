package ted.gun0912.sleep.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import ted.gun0912.sleep.common.extension.isNotNull
import ted.gun0912.sleep.design.extension.hideKeyboard
import ted.gun0912.sleep.ui.BR
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.extension.observe
import ted.gun0912.sleep.ui.extension.repeatOnStarted

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel<VE>, VE : ViewEvent>(
    @LayoutRes private val layoutResId: Int,
) : Fragment() {

    private var _binding: B? = null
    protected val binding: B
        get() = _binding ?: throw IllegalStateException("Fragment에서 Destroy 이후 binding에 접근하려고함")

    abstract val viewModel: VM

    abstract fun handleEvent(event: VE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding {
            lifecycleOwner = viewLifecycleOwner
            setVariable(BR.vm, viewModel)
            setVariable(BR.view, this@BaseFragment)
        }
        observeLibraryEvent()
        observeEvent()
    }

    private fun observeLibraryEvent() = repeatOnStarted {
        viewModel.libraryEventFlow
            .collect {
                when (it) {
                    is BaseViewModel.Event.Finish -> handleFinish(it.isComplete)
                    BaseViewModel.Event.HideKeyboard -> requireView().hideKeyboard()
                }
            }
    }

    private fun observeEvent() = repeatOnStarted {
        viewModel.eventFlow
            .collect { handleEvent(it) }
    }

    private fun handleFinish(isComplete: Boolean) {
        activity isNotNull { activity ->
            val result = if (isComplete) {
                Activity.RESULT_OK
            } else {
                Activity.RESULT_CANCELED
            }
            activity.setResult(result)
            activity.onBackPressed()
        }
    }

    protected fun binding(action: B.() -> Unit) {
        binding.run(action)
    }

    protected fun viewModel(action: VM.() -> Unit) {
        viewModel.run(action)
    }
//    private val logger : LoggerUtil = LoggerUtil().getInstance()

    private val logger : LoggerUtil = LoggerUtil("BASE_FRAGMENT").getInstance()

    protected infix fun <T> StateFlow<T?>.observe(action: (T) -> Unit) {
        observe(viewLifecycleOwner, action)
        logger.debug("observer is started")
    }
}
