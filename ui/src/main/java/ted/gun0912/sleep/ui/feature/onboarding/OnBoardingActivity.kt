package ted.gun0912.sleep.ui.feature.onboarding

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ted.gun0912.sleep.common.exception.ErrorUtil
import ted.gun0912.sleep.component.Haptic
import ted.gun0912.sleep.dataResource.collectDataResource
import ted.gun0912.sleep.domain.usecase.SaveMyDeviceUseCase
import ted.gun0912.sleep.model.BluetoothDevice
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.ActivityTransitionType
import ted.gun0912.sleep.ui.base.BaseActivity
import ted.gun0912.sleep.ui.base.template.ActivityTemplate
import ted.gun0912.sleep.ui.databinding.ActivityOnboardingBinding
import ted.gun0912.sleep.ui.extension.intentValue
import ted.gun0912.sleep.ui.extension.play
import ted.gun0912.sleep.ui.feature.main.MainActivity
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingViewModel.Event
import ted.gun0912.sleep.ui.feature.onboarding.intro.OnBoardingIntroFragment
import ted.gun0912.sleep.ui.feature.onboarding.permission.OnBoardingPermissionFragment
import ted.gun0912.sleep.ui.feature.onboarding.scan.OnBoardingEnvScanFragment
import ted.gun0912.sleep.ui.feature.onboarding.scan.OnBoardingScanFragment
import ted.gun0912.sleep.ui.feature.onboarding.verification.OnBoardingValidationFragment
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity :
    BaseActivity<ActivityOnboardingBinding, OnBoardingViewModel, Event>(
        R.layout.activity_onboarding
    ), OnBoardingListener {

    override val viewModel: OnBoardingViewModel by viewModels()

    @Inject
    lateinit var haptic: Haptic

    private val firstStep: OnBoardingStep by intentValue(EXTRA_STEP)

    private lateinit var viewPagerAdapter: OnBoardingPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTransition = ActivityTransitionType.FADE
        binding.exoPlayerView.play(lifecycle, R.string.video_onboarding)
//        binding.exoPlayerView.play(lifecycle, R.string.video_onboarding)
        setupViewPager()
    }

    override fun handleEvent(event: Event) = Unit

    private fun setupViewPager() {
        val fragments = listOf(
            OnBoardingIntroFragment.newInstance(),
            OnBoardingValidationFragment.newInstance(),
            OnBoardingPermissionFragment.newInstance(),
            OnBoardingScanFragment.newInstance(),
            OnBoardingEnvScanFragment.newInstance()
        )
        viewPagerAdapter = OnBoardingPagerAdapter(this, fragments)
        binding.viewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = viewPagerAdapter.itemCount
            adapter = viewPagerAdapter
            setCurrentItem(firstStep.position, false)
        }
    }

    override fun showNextStep(currentStep: OnBoardingStep) {
        if (currentStep == OnBoardingStep.SCAN_ENV) {
            ErrorUtil.logException("SCAN 단계에서는 showNextStep()가 호출될 수 없음")
        } else {
            haptic.success()
            binding.viewPager.setCurrentItem(currentStep.position + 1, true)
        }
    }

    override fun onSleepDeviceSelected(bluetoothDevice: BluetoothDevice) {
        viewModel.saveSleepDeviec(bluetoothDevice)
        haptic.success()
        binding.viewPager.setCurrentItem(4, true)
    }

    override fun onEnvDeviceSelected(bluetoothDevice: BluetoothDevice) {
        haptic.success()
        val envDeviceAddress = bluetoothDevice.address
        val Sleepdevice = viewModel.selectedSleepDevice.value

        viewModel.saveEnvDevice(bluetoothDevice)
        MainActivity.startActivity(
            this,
            Sleepdevice!!.address,
            envDeviceAddress,
        )
        finish()
    }

    override fun onBackPressed() {
        when {
            !isFirstStep() -> showPreStep()
            firstStep == OnBoardingStep.INTRO -> finishAffinity()
            else -> finish()
        }
    }

    private fun isFirstStep(): Boolean {
        val position = binding.viewPager.currentItem
        val step = OnBoardingStep.find(position)
        return step == firstStep
    }

    private fun showPreStep() {
        val currentPosition = binding.viewPager.currentItem
        binding.viewPager.setCurrentItem(currentPosition - 1, true)
    }

    companion object : ActivityTemplate<OnBoardingActivity>() {
        private const val EXTRA_STEP = "EXTRA_STEP"
        fun startActivity(context: Context, step: OnBoardingStep = OnBoardingStep.INTRO) =
            startActivity(context, EXTRA_STEP to step)
    }
}
