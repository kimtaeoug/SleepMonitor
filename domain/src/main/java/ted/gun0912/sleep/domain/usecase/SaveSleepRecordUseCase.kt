package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.StatisticRepository
import ted.gun0912.sleep.model.SensorInfo
import java.time.LocalDateTime
import javax.inject.Inject

class SaveSleepRecordUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {

    operator fun invoke(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<SensorInfo>,
        filePath: String
    ) = statisticRepository.saveSleepRecord(userId, sleepTime, wakeUpTime, records, filePath)
}
