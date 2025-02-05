package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.StatisticRepository
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import javax.inject.Inject

class GetDailySleepStatisticListUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {

    operator fun invoke(
        userId: String,
        sleepType: SleepType,
        startDate: LocalDate,
        endDate: LocalDate
    ) = statisticRepository.getDailySleepStatistics(userId, sleepType, startDate, endDate)
}
