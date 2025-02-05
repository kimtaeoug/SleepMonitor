package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.UserRepository
import ted.gun0912.sleep.model.InterviewType
import javax.inject.Inject


class GetInterviewQuestionListUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(interviewType: InterviewType) = userRepository.getInterviewQuestionList(interviewType)
}