package ted.gun0912.sleep.ui.feature.deprecated.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ted.gun0912.sleep.model.RangeDayType
import ted.gun0912.sleep.model.SleepType
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.EmptyViewEvent
import ted.gun0912.sleep.ui.base.EmptyViewModel
import ted.gun0912.sleep.ui.databinding.FragmentDashboardContainerBinding
import ted.gun0912.sleep.ui.feature.main.BaseTabFragment

class DashBoardContainerFragment :
    BaseTabFragment<FragmentDashboardContainerBinding, EmptyViewModel, EmptyViewEvent>(R.layout.fragment_dashboard_container) {
    override val viewModel: EmptyViewModel = EmptyViewModel()

    override fun handleEvent(event: EmptyViewEvent) {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabViewPager(SleepType.NIGHT)
        setupSleepTypeListener()
    }

    private fun setupTabViewPager(sleepType: SleepType) {
        val fragmentTitlePairs: List<Pair<Fragment, Int>> = listOf(
            DashboardFragment.newInstance(RangeDayType.WEEK, sleepType) to R.string.week,
            DashboardFragment.newInstance(RangeDayType.MONTH, sleepType) to R.string.month,
        )
        val adapter = DashBoardContainerPagerAdapter(this, fragmentTitlePairs)
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun setupSleepTypeListener() {
        binding.dropdownSleepType.setOnItemClickListener { _, _, position, _ ->
            val sleepType = SleepType.values()[position]
            setupTabViewPager(sleepType)
        }
    }
}
