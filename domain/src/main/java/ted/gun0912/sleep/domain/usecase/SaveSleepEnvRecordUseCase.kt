package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.StatisticRepository
import ted.gun0912.sleep.model.EnvSensorInfo
import java.time.LocalDateTime
import javax.inject.Inject

class SaveSleepEnvRecordUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {

    suspend operator fun invoke(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<EnvSensorInfo>
    ) = statisticRepository.saveSleepEnvRecord(userId, sleepTime, wakeUpTime, records)
}