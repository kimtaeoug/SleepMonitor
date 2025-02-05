package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.UserRepository
import ted.gun0912.sleep.model.InterviewAnswer
import ted.gun0912.sleep.model.InterviewType
import ted.gun0912.sleep.model.User
import javax.inject.Inject

class SaveInterviewAnswerUseCase @Inject constructor(private val userRepository: UserRepository) {

    operator fun invoke(
        userId: String,
        sleepDate: String,
        interviewType: InterviewType,
        answers: List<InterviewAnswer>
    ) = userRepository.saveInterviewAnswerList(userId, sleepDate, interviewType, answers)
}
