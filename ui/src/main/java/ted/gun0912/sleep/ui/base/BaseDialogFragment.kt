package ted.gun0912.sleep.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.flow.StateFlow
import ted.gun0912.sleep.common.extension.isNotNull
import ted.gun0912.sleep.design.extension.hideKeyboard
import ted.gun0912.sleep.ui.BR
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.extension.observe
import ted.gun0912.sleep.ui.extension.repeatOnStarted

abstract class BaseDialogFragment<B : ViewDataBinding, VM : BaseViewModel<VE>, VE : ViewEvent>(
    @LayoutRes private val layoutResId: Int
) : DialogFragment() {

    protected lateinit var binding: B
        private set

    abstract val viewModel: VM

    abstract fun handleEvent(event: VE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding {
            lifecycleOwner = viewLifecycleOwner
            setVariable(BR.vm, viewModel)
            setVariable(BR.view, this@BaseDialogFragment)
        }

        observeLibraryEvent()
        observeEvent()
        dialog?.window?.let { window ->
            window.attributes = WindowManager.LayoutParams().apply {
                copyFrom(window.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
        }
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
        }
        if (isComplete) {
            dismissAllowingStateLoss()
        } else {
            dialog?.cancel()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        manager.beginTransaction().let {
            it.add(this, tag)
            it.commitAllowingStateLoss()
        }
    }

    protected fun binding(action: B.() -> Unit) {
        binding.run(action)
    }

    protected fun viewModel(action: VM.() -> Unit) {
        viewModel.run(action)
    }

    protected infix fun <T> StateFlow<T?>.observe(action: (T) -> Unit) {
        observe(viewLifecycleOwner, action)
    }

}
