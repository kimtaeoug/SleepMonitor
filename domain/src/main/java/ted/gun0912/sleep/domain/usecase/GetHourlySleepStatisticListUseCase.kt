package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.StatisticRepository
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import javax.inject.Inject

class GetHourlySleepStatisticListUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {

    operator fun invoke(
        sleepType: SleepType,
        date: LocalDate,
    ) = statisticRepository.getHourlySleepStatistics(sleepType, date)
}
