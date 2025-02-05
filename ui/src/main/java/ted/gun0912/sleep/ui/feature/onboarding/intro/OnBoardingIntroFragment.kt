package ted.gun0912.sleep.ui.feature.onboarding.intro

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.template.FragmentTemplate
import ted.gun0912.sleep.ui.databinding.FragmentOnboardingIntroBinding
import ted.gun0912.sleep.ui.feature.onboarding.BaseOnBoardingFragment
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingStep
import ted.gun0912.sleep.ui.feature.onboarding.intro.OnBoardingIntroViewModel.Event

@AndroidEntryPoint
class OnBoardingIntroFragment :
    BaseOnBoardingFragment<FragmentOnboardingIntroBinding, OnBoardingIntroViewModel, Event>(
        R.layout.fragment_onboarding_intro, OnBoardingStep.INTRO
    ) {

    override val viewModel: OnBoardingIntroViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAction.setOnClickListener { showNextStep() }
    }

    override fun handleEvent(event: Event) = Unit

    companion object : FragmentTemplate<OnBoardingIntroFragment>()
}
