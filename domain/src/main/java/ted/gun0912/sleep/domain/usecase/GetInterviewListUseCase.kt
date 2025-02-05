package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.UserRepository
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import javax.inject.Inject

class GetInterviewListUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(userId: String) = userRepository.getInterviews(userId)
}