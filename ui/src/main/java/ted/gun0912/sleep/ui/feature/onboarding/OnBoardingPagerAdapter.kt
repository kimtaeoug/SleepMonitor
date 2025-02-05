package ted.gun0912.sleep.ui.feature.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingPagerAdapter(
    activity: FragmentActivity,
    private val fragmentIconPairs: List<BaseOnBoardingFragment<*, *, *>>,
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = fragmentIconPairs.size

    override fun createFragment(position: Int): Fragment = getFragment(position)

    private fun getFragment(position: Int) = fragmentIconPairs[position]

}
