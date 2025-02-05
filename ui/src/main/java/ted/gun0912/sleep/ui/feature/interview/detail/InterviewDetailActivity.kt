package ted.gun0912.sleep.ui.feature.interview.detail

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.sleep.model.InterviewType
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.base.BaseActivity
import ted.gun0912.sleep.ui.base.template.ActivityTemplate
import ted.gun0912.sleep.ui.databinding.ActivitySleepInterviewBinding
import ted.gun0912.sleep.ui.feature.interview.detail.compose.InterviewRoute
import ted.gun0912.sleep.ui.feature.splash.SplashViewModel

@AndroidEntryPoint
class InterviewDetailActivity() :
    BaseActivity<ActivitySleepInterviewBinding, InterviewDetailViewModel, InterviewDetailViewModel.Event>(
        R.layout.activity_sleep_interview
    ) {

    override val viewModel: InterviewDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val composeView = binding.composeView
        composeView.setContent {
            MaterialTheme {
                InterviewRoute(
                    onNavUp = { finish() },
                    viewModel = viewModel
                )
            }
        }
    }

    override fun handleEvent(event: InterviewDetailViewModel.Event) = when (event) {
        InterviewDetailViewModel.Event.showMain -> {
            finish()
        }
    }

    companion object : ActivityTemplate<InterviewDetailActivity>() {

        private const val KEY_INTERVIEW_DATE = "KEY_INTERVIEW_DATE"
        private const val KEY_INTERVIEW_TYPE = "KEY_INTERVIEW_TYPE"

        fun startActivity(context: Context, date: String, type: InterviewType) =
            InterviewDetailActivity.startActivity(
                context,
                KEY_INTERVIEW_DATE to date,
                KEY_INTERVIEW_TYPE to type
            ) // TODO
    }
}