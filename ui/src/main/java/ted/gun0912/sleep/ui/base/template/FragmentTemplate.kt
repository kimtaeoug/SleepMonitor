package ted.gun0912.sleep.ui.base.template

import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
abstract class FragmentTemplate<out F : Fragment> {

    private val fragmentClz = ((this.javaClass.genericSuperclass as ParameterizedType)
        .actualTypeArguments[0] as Class<F>)

    private val TAG = fragmentClz.simpleName

    fun show(
        fragmentManager: FragmentManager,
        @IdRes containerViewId: Int,
        vararg extras: Pair<String, Any?>,
    ): F =
        find(fragmentManager) ?: newInstance().apply {
            arguments = bundleOf(*extras)
            fragmentManager.beginTransaction()
                .replace(containerViewId, this, TAG)
                .commitAllowingStateLoss()
        }

    fun show(
        activity: FragmentActivity,
        @IdRes containerViewId: Int,
        vararg extras: Pair<String, Any?>,
    ): F =
        show(
            fragmentManager = activity.supportFragmentManager,
            containerViewId = containerViewId,
            extras = extras
        )

    fun show(
        fragment: Fragment,
        @IdRes containerViewId: Int,
        vararg extras: Pair<String, Any?>,
    ): F =
        show(
            fragmentManager = fragment.childFragmentManager,
            containerViewId = containerViewId,
            extras = extras
        )

    fun find(fragmentManager: FragmentManager): F? {
        return fragmentManager.findFragmentByTag(TAG) as F?
    }

    fun newInstance(): F = fragmentClz.newInstance()

    fun isShowing(fragmentManager: FragmentManager) = find(fragmentManager) != null

    fun isNotShowing(fragmentManager: FragmentManager) = !isShowing(fragmentManager)

}
