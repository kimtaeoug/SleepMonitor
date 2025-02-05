package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.ConfigRepository
import java.time.LocalTime
import javax.inject.Inject

class SaveLastAlarmTimeUseCase @Inject constructor(private val configRepository: ConfigRepository) {

    operator fun invoke(time: LocalTime) = configRepository.saveAlarmTime(time)
}
