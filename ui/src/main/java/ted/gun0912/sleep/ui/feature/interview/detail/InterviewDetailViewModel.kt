package ted.gun0912.sleep.ui.feature.interview.detail

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ted.gun0912.sleep.domain.usecase.GetInterviewQuestionListUseCase
import ted.gun0912.sleep.domain.usecase.GetUserIdUseCase
import ted.gun0912.sleep.domain.usecase.SaveInterviewAnswerUseCase
import ted.gun0912.sleep.domain.usecase.SendInterviewMessageUseCase
import ted.gun0912.sleep.model.AnswerOption
import ted.gun0912.sleep.model.InterviewAnswer
import ted.gun0912.sleep.model.InterviewQuestion
import ted.gun0912.sleep.model.InterviewType
import ted.gun0912.sleep.model.SingleChoiceAnswer
import ted.gun0912.sleep.model.TextAnswer
import ted.gun0912.sleep.model.TimeAnswer
import ted.gun0912.sleep.model.TimeDurationAnswer
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.base.BaseViewModel
import ted.gun0912.sleep.ui.base.ViewEvent
import ted.gun0912.sleep.ui.extension.getNotNull
import java.util.logging.Logger
import javax.inject.Inject
import kotlin.math.log

const val TOTAL_CHAT_CYCLE = 3

@HiltViewModel
class InterviewDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getInterviewQuestionListUseCase: GetInterviewQuestionListUseCase,
    private val saveInterviewAnswerUseCase: SaveInterviewAnswerUseCase,
    private val sendInterviewMessageUseCase: SendInterviewMessageUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : BaseViewModel<InterviewDetailViewModel.Event>() {
    private val logger: LoggerUtil = LoggerUtil("InterviewDetailViewModel").getInstance()

    private var chatCycleCount = 0

    private var userId: String? = ""

    private var questionOrder: List<InterviewQuestion> = listOf()

    private var questionIndex = 0

    private val interviewType = savedStateHandle.getNotNull<InterviewType>(KEY_INTERVIEW_TYPE)
    private val sleepDate = savedStateHandle.getNotNull<String>(KEY_INTERVIEW_DATE)

    private var answers = mutableListOf<InterviewAnswer>()

    val interviewScreenData: InterviewScreenData?
        get() = _interviewScreenData.value

    private val _isNextEnabled = mutableStateOf(false)
    val isNextEnabled: Boolean
        get() = _isNextEnabled.value

    private var chatResponseMessage: String? = null

    private val _interviewScreenData = mutableStateOf(createInterviewScreenData())

    init {
        viewModelScope.launch {
            launch {
                logger.info("InterviewDetailViewModel is launched")
                userId = getUserIdUseCase.invoke().await()
            }
            getInterviewQuestionListUseCase(interviewType).collectDataResource(
                onSuccess = { list ->
                    questionOrder = list
                    _interviewScreenData.value = createInterviewScreenData()
                    logger.error("questionOrder : $questionOrder")
                    logger.error("_interviewScreenData.value : ${_interviewScreenData.value}")
                }
            )
            sendInterviewMessageUseCase(userId!!, "안녕하세요").collectDataResource(
                onSuccess = {
                    chatResponseMessage = it
                    _interviewScreenData.value = createInterviewScreenData()
                    logger.error("chatResponseMessage : $chatResponseMessage")
                    logger.error("_interviewScreenData.value : ${_interviewScreenData.value}")
                }
            )
        }
    }

    fun onChatResponse(message: String) {
        logger.error("onChatResponse is invoked")
        // todo
        chatResponseMessage = null
        _interviewScreenData.value = createInterviewScreenData()

        viewModelScope.launch {
            sendInterviewMessageUseCase(userId!!, message).collectDataResource(
                onSuccess = { message ->
                    chatResponseMessage = message
                    chatCycleCount += 1
                    _interviewScreenData.value = createInterviewScreenData()
                }
            )
        }
    }

    fun onTimeResponse(timestamp: Long) {
        addOrSetAnswer(TimeAnswer(questionOrder[questionIndex].id, timestamp))
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onTimeDurationResponse(hour: Int?, min: Int?) {
        addOrSetAnswer(TimeDurationAnswer(questionOrder[questionIndex].id, hour, min))
        val answer = answers.getOrNull(questionIndex)
        _isNextEnabled.value =
            answer is TimeDurationAnswer && answer.hour != null && answer.min != null
    }

    fun onSingleChoiceResponse(index: Int, option: AnswerOption) {
        addOrSetAnswer(SingleChoiceAnswer(questionOrder[questionIndex].id, index))
        _isNextEnabled.value = getIsNextEnabled()
    }

    private fun addOrSetAnswer(answer: InterviewAnswer) {
        if (answers.getOrNull(questionIndex) != null) {
            answers.set(questionIndex, answer)
        } else {
            answers.add(answer)
        }
    }

    fun changePrevQuestion() {
        questionIndex -= 1
        _interviewScreenData.value = createInterviewScreenData()
    }

    fun changeNextQuestion() {
        questionIndex += 1
        _interviewScreenData.value = createInterviewScreenData()
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun completeInterview() {
        viewModelScope.launch {
            saveInterviewAnswerUseCase(
                userId!!,
                sleepDate,
                interviewType,
                answers
            ).collectDataResource(
                onSuccess = {
                    event(Event.showMain)
                }
            )
        }
    }

    private fun getIsNextEnabled(): Boolean {
        return answers.getOrNull(questionIndex) != null
    }

    private fun createInterviewScreenData(): InterviewScreenData {

        logger.error("createInterviewScreenData is invoked")
        val isChatInterviewDone = chatCycleCount >= TOTAL_CHAT_CYCLE

        return InterviewScreenData(
            shouldShowPreviousButton = if (!isChatInterviewDone) false else questionIndex > 0,
            shouldShowDoneButton = if (!isChatInterviewDone) false else questionIndex == questionOrder.size - 1,
            question = if (questionOrder.isEmpty()) null else questionOrder[questionIndex],
            questionIndex = questionIndex,
            isChatInterviewDone = isChatInterviewDone,
            message = chatResponseMessage
        )
    }

    sealed interface Event : ViewEvent {
        object showMain : Event
    }

    companion object {
        const val KEY_SELECTED_DATE = "KEY_SELECTED_DATE"
        const val KEY_INTERVIEW_DATE = "KEY_INTERVIEW_DATE"
        const val KEY_INTERVIEW_TYPE = "KEY_INTERVIEW_TYPE"
    }
}

data class InterviewScreenData(
    val shouldShowPreviousButton: Boolean,
    val shouldShowDoneButton: Boolean,
    val question: InterviewQuestion?,
    val questionIndex: Int,
    val isChatInterviewDone: Boolean,
    val message: String?
)