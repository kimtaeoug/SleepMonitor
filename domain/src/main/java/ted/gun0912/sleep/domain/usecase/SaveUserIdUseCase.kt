package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.StatisticRepository
import ted.gun0912.sleep.domain.repository.UserRepository
import ted.gun0912.sleep.model.SensorInfo
import java.time.LocalDateTime
import javax.inject.Inject

class SaveUserIdUseCase @Inject constructor(private val userRepository: UserRepository) {

    operator fun invoke(
        userId: String
    ) = userRepository.saveUserId(userId)
}
