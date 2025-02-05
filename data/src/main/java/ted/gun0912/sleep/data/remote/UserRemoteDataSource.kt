package ted.gun0912.sleep.data.remote

import kotlinx.coroutines.flow.Flow
import ted.gun0912.sleep.dataResource.DataResource
import ted.gun0912.sleep.model.EnvSensorInfo
import ted.gun0912.sleep.model.Interview
import ted.gun0912.sleep.model.InterviewAnswer
import ted.gun0912.sleep.model.InterviewQuestion
import ted.gun0912.sleep.model.InterviewType
import ted.gun0912.sleep.model.SensorInfo
import java.time.LocalDateTime
import java.time.LocalTime

interface UserRemoteDataSource {

    suspend fun saveSleepRecords(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        filePath: String
    )

    suspend fun saveSleepEnvRecords(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        records: List<EnvSensorInfo>
    )

    suspend fun getInterviews(userId: String): List<Interview>

    suspend fun getInterviewQuestions(interviewType: InterviewType): List<InterviewQuestion>

    suspend fun updateInterviewAnswers(
        userId: String,
        date: String,
        interviewType: InterviewType,
        answers: List<InterviewAnswer>
    )

    suspend fun sendMessage(
        userId: String,
        message: String
    ): String?

    suspend fun validateUser(
        userId: String
    )

    fun saveUserId(userId: String): Unit

    fun getUserId(): String
}