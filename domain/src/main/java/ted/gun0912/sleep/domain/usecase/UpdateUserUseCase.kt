package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.UserRepository
import ted.gun0912.sleep.model.User
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(private val userRepository: UserRepository) {

    operator fun invoke(user: User) = userRepository.updateUser(user)
}
