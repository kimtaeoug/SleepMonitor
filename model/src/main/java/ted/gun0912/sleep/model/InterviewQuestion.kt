package ted.gun0912.sleep.model

data class InterviewQuestion(
    val id: String,
    val type: InterviewQuestionType,
    val question: String,
    val answers: List<AnswerOption>
)

data class AnswerOption(
    val answer: String
)
