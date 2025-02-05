package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {

    operator fun invoke() = userRepository.getUser()
}
