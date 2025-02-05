package ted.gun0912.sleep.ui.feature.interview

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ted.gun0912.sleep.domain.usecase.GetInterviewListUseCase
import ted.gun0912.sleep.domain.usecase.GetUserIdUseCase
import ted.gun0912.sleep.model.Interview
import ted.gun0912.sleep.model.InterviewType
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val getInterviewListUseCase: GetInterviewListUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : BaseViewModel<InterviewViewModel.Event>() {

    private var userId: String? = ""

    private val _interviews = MutableStateFlow<List<Interview>?>(null)
    val interviews = _interviews.asStateFlow()
    private val logger : LoggerUtil = LoggerUtil("InterviewViewModel").getInstance()
    init {
        launch {
            userId = getUserIdUseCase.invoke().await()
        }
        logger.debug("InterviewViewModel is init")
        updateInterviews()
    }

    fun updateInterviews(){
        launch {
            logger.debug("updateInterviews is invoked")
            logger.debug("value : ${getInterviewListUseCase(userId!!).await()}")
            _interviews.value = getInterviewListUseCase(userId!!).await() ?: return@launch

        }
    }

    fun startSleepInterview(info: Interview) {
        logger.debug("startSleepInterview is invoked\ninfo : $info")
        event(Event.StartInterview(info.date, InterviewType.SLEEP))
    }

    fun startPainInterview(info: Interview) {
        logger.debug("startPainInterview is invoked\ninfo : $info")
        event(Event.StartInterview(info.date, InterviewType.PAIN))
    }

    sealed interface Event : ViewEvent {
        data class StartInterview(val date: String, val type: InterviewType) : InterviewViewModel.Event
    }
}