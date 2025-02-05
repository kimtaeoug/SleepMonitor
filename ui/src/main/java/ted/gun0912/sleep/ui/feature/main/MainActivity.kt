package ted.gun0912.sleep.ui.feature.main

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2

import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.ActivityTransitionType
import ted.gun0912.sleep.ui.base.BaseActivity
import ted.gun0912.sleep.ui.base.template.ActivityTemplate
import ted.gun0912.sleep.ui.databinding.ActivityMainBinding
import ted.gun0912.sleep.ui.extension.intentValue
import ted.gun0912.sleep.ui.feature.history.HistoryFragment
import ted.gun0912.sleep.ui.feature.interview.InterviewFragment
import ted.gun0912.sleep.ui.feature.main.MainViewModel.Event
import ted.gun0912.sleep.ui.feature.setting.SettingFragment
import ted.gun0912.sleep.ui.feature.sleep.SleepFragment

//import ted.gun0912.sleep.ui.feature.sleep.SleepFragment


@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding, MainViewModel, Event>(R.layout.activity_main) {

    override val viewModel: MainViewModel by viewModels()

    private lateinit var viewPagerAdapter: MainViewPagerAdapter
    private val sleepDeviceAddress: String by intentValue(EXTRA_SLEEP_DEVICE_ADDRESS)
    private val envDeviceAddress: String by intentValue(EXTRA_ENV_DEVICE_ADDRESS)

    private lateinit var sleepFragment: SleepFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTransition = ActivityTransitionType.FADE
        setupViewPager()
        setupBottomNavigation()
    }

    override fun handleEvent(event: Event) = Unit

    private fun setupViewPager() {
        sleepFragment = SleepFragment.newInstance(sleepDeviceAddress, envDeviceAddress)
        val fragmentIconPairs =
            listOf(
                sleepFragment to R.id.navigation_sleep,
                HistoryFragment() to R.id.navigation_history,
                InterviewFragment() to R.id.navigation_interview,
                SettingFragment() to R.id.navigation_setting,
            )
        viewPagerAdapter = MainViewPagerAdapter(this, fragmentIconPairs)
        binding.viewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = viewPagerAdapter.itemCount
            adapter = viewPagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.navView.menu.getItem(position).isChecked = true
                }
            }
            )
        }
    }

    private fun setupBottomNavigation() {
        binding.navView.setOnItemSelectedListener { item ->
            val selectedPosition = viewPagerAdapter.getPosition(item.itemId)
            if (binding.viewPager.currentItem != selectedPosition) {
                binding.viewPager.setCurrentItem(selectedPosition, false)
            }
            true
        }
    }

    override fun onBackPressed() {
        if (!sleepFragment.isSleepStarted) {
            finishAffinity()
        } else {
            if (binding.viewPager.currentItem != 0) {
                binding.viewPager.setCurrentItem(0, true)
            }
        }
    }

    companion object : ActivityTemplate<MainActivity>() {
        private const val EXTRA_SLEEP_DEVICE_ADDRESS = "EXTRA_SLEEP_DEVICE_ADDRESS"
        private const val EXTRA_ENV_DEVICE_ADDRESS = "EXTRA_ENV_DEVICE_ADDRESS"
        fun startActivity(context: Context, sleepDeviceAddress: String, envDeviceAddress: String) =
            startActivity(
                context,
                EXTRA_SLEEP_DEVICE_ADDRESS to sleepDeviceAddress,
                EXTRA_ENV_DEVICE_ADDRESS to envDeviceAddress
            )
    }
}
