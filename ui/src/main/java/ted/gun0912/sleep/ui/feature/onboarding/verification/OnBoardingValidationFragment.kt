package ted.gun0912.sleep.ui.feature.onboarding.verification

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.template.FragmentTemplate
import ted.gun0912.sleep.ui.databinding.FragmentOnboardingScanBinding
import ted.gun0912.sleep.ui.databinding.FragmentOnboardingValidationBinding
import ted.gun0912.sleep.ui.feature.onboarding.BaseOnBoardingFragment
import ted.gun0912.sleep.ui.feature.onboarding.OnBoardingStep
import ted.gun0912.sleep.ui.feature.onboarding.intro.OnBoardingIntroFragment
import ted.gun0912.sleep.ui.feature.onboarding.scan.OnBoardingScanViewModel

@AndroidEntryPoint
class OnBoardingValidationFragment :
    BaseOnBoardingFragment<FragmentOnboardingValidationBinding, OnBoardingValidationViewModel, OnBoardingValidationViewModel.Event>(
        R.layout.fragment_onboarding_validation, OnBoardingStep.VALIDATION
    ) {

    override val viewModel: OnBoardingValidationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isUser.observe { isUser ->
            if (isUser) showNextStep()
        }

        binding.btnAction.setOnClickListener {
            Log.d("ID BUTTON", "input : " + binding.editUserId.text)
            viewModel.validateAccount(binding.editUserId.text.toString())
        }
    }

    override fun handleEvent(event: OnBoardingValidationViewModel.Event) {
        TODO("Not yet implemented")
    }

    companion object : FragmentTemplate<OnBoardingValidationFragment>()
}