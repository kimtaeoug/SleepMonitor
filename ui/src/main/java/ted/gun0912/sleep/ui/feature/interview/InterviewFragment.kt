package ted.gun0912.sleep.ui.feature.interview

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ted.gun0912.sleep.model.InterviewType
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.databinding.FragmentInterviewBinding
import ted.gun0912.sleep.ui.feature.interview.detail.InterviewDetailActivity
import ted.gun0912.sleep.ui.feature.main.BaseTabFragment


@AndroidEntryPoint
class InterviewFragment :
    BaseTabFragment<FragmentInterviewBinding, InterviewViewModel, InterviewViewModel.Event>(
        R.layout.fragment_interview
    ) {

    private val logger: LoggerUtil = LoggerUtil("InterviewFragment").getInstance()
    override val viewModel: InterviewViewModel by viewModels()

    override fun onStart() {
        logger.debug("onStart is invoked")
        viewModel.updateInterviews()
        super.onStart()
    }

    override fun handleEvent(event: InterviewViewModel.Event) {
        when (event) {
            is InterviewViewModel.Event.StartInterview -> {
                startInterview(event.date, event.type)
            }
        }
    }

    private fun startInterview(date: String, interviewType: InterviewType) {
        context?.let { InterviewDetailActivity.startActivity(it, date, interviewType) }
    }
}
