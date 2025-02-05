package ted.gun0912.sleep.ui.base.template

import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
abstract class DialogFragmentTemplate<out F : DialogFragment> {

    private val fragmentClz = ((this.javaClass.genericSuperclass as ParameterizedType)
        .actualTypeArguments[0] as Class<F>)

    private val TAG = fragmentClz.simpleName

    fun show(
        fragmentManager: FragmentManager,
        vararg extras: Pair<String, Any?>,
        tag: String = TAG,
    ): F =
        find(fragmentManager) ?: newInstance().apply {
            arguments = bundleOf(*extras)
            show(fragmentManager, tag)
        }

    fun show(
        activity: FragmentActivity,
        vararg extras: Pair<String, Any?>,
        tag: String = TAG,
    ): F = show(fragmentManager = activity.supportFragmentManager, extras = extras, tag = tag)

    fun show(
        fragment: Fragment,
        vararg extras: Pair<String, Any?>,
        tag: String = TAG,
    ): F =
        show(
            fragmentManager = fragment.parentFragmentManager,
            extras = extras,
            tag = tag
        ).apply {
            setTargetFragment(fragment, -1)
        }

    fun find(
        fragmentManager: FragmentManager,
        tag: String = TAG,
    ): F? {
        return fragmentManager.findFragmentByTag(tag) as F?
    }

    private fun newInstance(): F = fragmentClz.newInstance()

    fun isShowing(fragmentManager: FragmentManager) = find(fragmentManager) != null

    fun isNotShowing(fragmentManager: FragmentManager) = !isShowing(fragmentManager)

}

