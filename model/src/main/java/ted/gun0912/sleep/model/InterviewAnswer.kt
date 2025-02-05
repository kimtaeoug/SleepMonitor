package ted.gun0912.sleep.model

sealed class InterviewAnswer(
    open val questionId: String
)

data class TextAnswer(override val questionId: String, val text: String) : InterviewAnswer(questionId)

data class TimeAnswer(override val questionId: String, val timestamp: Long) : InterviewAnswer(questionId)

data class TimeDurationAnswer(override val questionId: String, val hour: Int?, val min: Int?) : InterviewAnswer(questionId)

data class SingleChoiceAnswer(override val questionId: String, val index: Int) : InterviewAnswer(questionId)
