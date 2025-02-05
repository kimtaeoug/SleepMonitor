package ted.gun0912.sleep.domain.usecase

import ted.gun0912.sleep.domain.repository.StatisticRepository
import ted.gun0912.sleep.model.SleepType
import javax.inject.Inject

class GetFirstRecordDayUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {

    operator fun invoke(sleepType: SleepType) = statisticRepository.getFirstRecordDay(sleepType)
}
