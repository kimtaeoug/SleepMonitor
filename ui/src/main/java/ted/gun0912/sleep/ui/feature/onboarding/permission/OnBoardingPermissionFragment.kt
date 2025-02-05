package ted.gun0912.sleep.ui.feature.onboarding.permission

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ted.gun0912.sleep.component.Haptic
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.template.FragmentTemplate
import ted.gun0912.sleep.ui.databinding.FragmentOnboardingPermissionBinding
import ted.gun0912.sleep.ui.feature.onboarding.BaseOnBoardingFragment
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingStep
import ted.gun0912.sleep.ui.feature.onboarding.permission.OnBoardingPermissionViewModel.*
import ted.gun0912.sleep.ui.permission.PermissionUtil
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingPermissionFragment :
    BaseOnBoardingFragment<FragmentOnboardingPermissionBinding, OnBoardingPermissionViewModel, Event>(
        R.layout.fragment_onboarding_permission, OnBoardingStep.PERMISSION
    ) {

    override val viewModel: OnBoardingPermissionViewModel by viewModels()

    @Inject
    lateinit var haptic: Haptic

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }

    private fun setupListener() {
        binding.btnAction.setOnClickListener {
            haptic.interact()
            requestPermission()
        }
    }

    private fun requestPermission() = lifecycleScope.launch {
        val hasPermission = PermissionUtil.hasPermission(requireContext())
        if (hasPermission) {
            showNextStep()
            return@launch
        }

        val isGranted = PermissionUtil.requestPermission(requireActivity())
        if (isGranted) {
            showNextStep()
        }
    }

    override fun handleEvent(event: Event) = Unit

    companion object : FragmentTemplate<OnBoardingPermissionFragment>()
}
