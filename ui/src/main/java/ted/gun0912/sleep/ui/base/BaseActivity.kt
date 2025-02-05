package ted.gun0912.sleep.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.coroutines.flow.StateFlow
import ted.gun0912.sleep.design.extension.hideKeyboard
import ted.gun0912.sleep.ui.BR
import ted.gun0912.sleep.ui.extension.getIntentNullableValue
import ted.gun0912.sleep.ui.extension.observe
import ted.gun0912.sleep.ui.extension.putExtra
import ted.gun0912.sleep.ui.extension.repeatOnStarted
import ted.gun0912.sleep.ui.extension.setTransparentStatusBar

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel<VE>, VE : ViewEvent>(
    @LayoutRes private val layoutResId: Int
) : AppCompatActivity() {

    protected lateinit var binding: B
    protected abstract val viewModel: VM

    abstract fun handleEvent(event: VE)

    open var activityTransition: ActivityTransitionType? = null
        set(value) {
            field = getIntentNullableValue(EXTRA_ACTIVITY_TRANSITION) ?: value
            field?.run {
                overridePendingTransition(showFrontAnimation, hideBackAnimation)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTransition = ActivityTransitionType.RIGHT_TO_LEFT
        binding = DataBindingUtil.setContentView(this, layoutResId) ?: return
        binding {
            lifecycleOwner = this@BaseActivity
            setVariable(BR.vm, viewModel)
            setVariable(BR.view, this@BaseActivity)
        }
        observeBaseEvent()
        observeEvent()
        setTransparentStatusBar()
    }

    private fun observeBaseEvent() = repeatOnStarted {
        viewModel.libraryEventFlow
            .collect {
                when (it) {
                    is BaseViewModel.Event.Finish -> handleFinish(it.isComplete, it.data)
                    BaseViewModel.Event.HideKeyboard -> currentFocus?.hideKeyboard()
                }
            }
    }

    private fun observeEvent() = repeatOnStarted {
        viewModel.eventFlow
            .collect { handleEvent(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    protected fun binding(action: B.() -> Unit) {
        binding.run(action)
    }

    protected fun viewModel(action: VM.() -> Unit) {
        viewModel.run(action)
    }

    protected infix fun <T> StateFlow<T?>.observe(action: (T) -> Unit) {
        observe(this@BaseActivity, action)
    }


    private fun handleFinish(isComplete: Boolean, data: Map<String, Any?>?) {
        val result = if (isComplete) {
            Activity.RESULT_OK
        } else {
            Activity.RESULT_CANCELED
        }

        val intent = Intent().apply {
            data?.entries?.forEach { (key, value) ->
                putExtra(key, value)
            }
        }
        setResult(result, intent)
        finish()
    }

    override fun finish() {
        super.finish()
        activityTransition?.let {
            overridePendingTransition(it.returnBackAnimation, it.finishFrontAnimation)
        }
    }

    companion object {
        const val EXTRA_ACTIVITY_TRANSITION = "EXTRA_ACTIVITY_TRANSITION"
    }
}
