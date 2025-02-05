package ted.gun0912.sleep.ui.feature.main

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter(
    activity: FragmentActivity,
    private val fragmentIdPairs: List<Pair<BaseTabFragment<*, *, *>, Int>>,
) : FragmentStateAdapter(activity) {

    private fun getFragment(position: Int) = fragmentIdPairs[position].first

    override fun getItemCount(): Int = fragmentIdPairs.size

    fun getPosition(@IdRes id: Int) =
        fragmentIdPairs.indexOfFirst { (fragment, fragmentId) -> id == fragmentId }

    override fun createFragment(position: Int): Fragment = getFragment(position)
}
