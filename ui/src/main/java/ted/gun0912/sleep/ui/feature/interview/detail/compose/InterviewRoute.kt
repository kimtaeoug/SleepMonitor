package ted.gun0912.sleep.ui.feature.interview.detail.compose

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ted.gun0912.sleep.model.AnswerOption
import ted.gun0912.sleep.model.InterviewQuestionType
import ted.gun0912.sleep.model.InterviewType
import ted.gun0912.sleep.ui.LoggerUtil
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.feature.interview.detail.InterviewDetailViewModel
import ted.gun0912.sleep.ui.feature.interview.detail.compose.question.SingleChoiceQuestion
import ted.gun0912.sleep.ui.feature.interview.detail.compose.question.TextQuestion
import ted.gun0912.sleep.ui.feature.interview.detail.compose.question.TimeDutaionQuestion
import ted.gun0912.sleep.ui.feature.interview.detail.compose.question.TimeQuestion
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

private val lgoger = LoggerUtil("InterviewRoute").getInstance()

@Composable
fun InterviewRoute(
    onNavUp: () -> Unit,
    viewModel: InterviewDetailViewModel
) {

    val screenData = viewModel.interviewScreenData ?: return
    val context = LocalContext.current

    val nextEnabled = if (!screenData.isChatInterviewDone) {
        if (screenData.message.isNullOrEmpty()) false else true
    } else {
        viewModel.isNextEnabled
    }

    var value by remember { mutableStateOf("") } // todo to viewmodel

    InterviewScreen(
        screenData = screenData,
        onClosePressed = { onNavUp() },
        isNextEnabled = nextEnabled,
        onPreviousPressed = { viewModel.changePrevQuestion() },
        onNextPressed = {
            if (!screenData.isChatInterviewDone) {
                lgoger.error("here")
                viewModel.onChatResponse(value) // todo
                value = "" // todo
            } else {
                lgoger.error("here")
                viewModel.changeNextQuestion()
            }
        },
        onDonePressed = { viewModel.completeInterview() },
    ) { paddings ->
        val modifier = Modifier.padding(paddings)

        if (!screenData.isChatInterviewDone) {
            lgoger.error("isChatInterviewDone")
            TextQuestion(
                title = screenData?.message ?: "", // todo nullable
                directionsResourceId = R.string.input_message,
                value = value,
                onValueChange = { value = it },
                onImeActionPerformed = {
                    viewModel.onChatResponse(value) // todo
                    value = ""
                },
                modifier = modifier
            )
        } else {
            when (screenData.question?.type) {
                InterviewQuestionType.SINGLE_CHOICE -> {
                    lgoger.error("SINGLE_CHOICE")

                    var selectedAnswer by remember { mutableStateOf<AnswerOption?>(null) }

                    LaunchedEffect(key1 = screenData.questionIndex) {
                        selectedAnswer = null
                    }

                    SingleChoiceQuestion(
                        title = screenData?.question?.question!!,
                        directionsResourceId = R.string.select_one,
                        possibleAnswers = screenData.question.answers,
                        selectedAnswer = selectedAnswer,
                        onOptionSelected = { index, option ->
                            selectedAnswer = option
                            viewModel.onSingleChoiceResponse(index, option)
                        },
                        modifier = modifier
                    )
                }

                InterviewQuestionType.TIME -> {
                    lgoger.error("TIME")

                    val calendar = Calendar.getInstance()
                    var timeText: String? by remember { mutableStateOf(null) }
                    val timePicker = createTimePickerDialog(
                        calendar = calendar,
                        context = context,
                    ) { hour, min ->
                        timeText = "${hour}시 ${min}분"
                        val localDateTime = LocalDateTime.now() // 현재 날짜와 시간
                            .withHour(hour)
                            .withMinute(min)

                        val timestamp = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
                        viewModel.onTimeResponse(timestamp)
                    }

                    TimeQuestion(
                        title = screenData?.question?.question!!,
                        value = timeText ?: "시간을 선택해주세요",
                        directionsResourceId = R.string.select_time,
                        onClick = { timePicker.show() },
                        modifier = modifier
                    )
                }

                InterviewQuestionType.DURATION_TIME -> {
                    lgoger.error("DURATION_TIME")

                    var selectedHour by remember { mutableStateOf<String>("") }
                    var selectedMin by remember { mutableStateOf<String>("") }

                    TimeDutaionQuestion(
                        title = screenData?.question?.question!!,
                        directionsResourceId = R.string.input_time,
                        hour = selectedHour,
                        min = selectedMin,
                        onHourChange = {
                            selectedHour = it
                            viewModel.onTimeDurationResponse(
                                selectedHour.toIntOrNull(),
                                selectedHour.toIntOrNull()
                            )
                        },
                        onMinChange = {
                            selectedMin = it
                            viewModel.onTimeDurationResponse(
                                selectedHour.toIntOrNull(),
                                selectedHour.toIntOrNull()
                            )
                        },
                        modifier = modifier
                    )
                }
            }
        }
    }
}

private fun createTimePickerDialog(
    calendar: Calendar,
    context: Context,
    onTimeSelected: (Int, Int) -> Unit
): TimePickerDialog {
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]

    return TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            onTimeSelected(selectedHour, selectedMinute)
        }, hour, minute, false
    )
}

