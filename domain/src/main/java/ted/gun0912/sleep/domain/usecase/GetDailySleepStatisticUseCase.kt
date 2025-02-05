package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.dataResource.mapDataResource
import ted.gun0912.sleep.domain.repository.StatisticRepository
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import javax.inject.Inject

class GetDailySleepStatisticUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {

    operator fun invoke(
        userId: String,
        sleepType: SleepType,
        date: LocalDate
    ) =
        statisticRepository.getDailySleepStatistics(userId, sleepType, date, date)
            .mapDataResource { it.first() }
}
