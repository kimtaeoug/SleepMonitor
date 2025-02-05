package ted.gun0912.sleep.ui.feature.deprecated.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter

class DashBoardContainerPagerAdapter(
    private val fragment: Fragment,
    private val fragmentTitlePairs: List<Pair<Fragment, Int>>
) : FragmentStatePagerAdapter(
    fragment.childFragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int = fragmentTitlePairs.size

    override fun getItem(position: Int): Fragment = fragmentTitlePairs[position].first

    override fun getPageTitle(position: Int): String {
        val titleResId = fragmentTitlePairs[position].second
        return fragment.getString(titleResId)
    }

}
