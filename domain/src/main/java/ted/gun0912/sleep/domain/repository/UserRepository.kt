package ted.gun0912.sleep.domain.repository

import kotlinx.coroutines.flow.Flow
import ted.gun0912.sleep.dataResource.DataResource
import ted.gun0912.sleep.model.Interview
import ted.gun0912.sleep.model.InterviewAnswer
import ted.gun0912.sleep.model.InterviewQuestion
import ted.gun0912.sleep.model.InterviewType
import ted.gun0912.sleep.model.SensorInfo
import ted.gun0912.sleep.model.User
import java.time.LocalDateTime
import java.time.LocalTime

interface UserRepository {

    fun getUser(): Flow<DataResource<User?>>

    fun updateUser(user: User): Flow<DataResource<Unit>>

    fun getInterviews(userId: String): Flow<DataResource<List<Interview>>>

    fun saveInterviewAnswerList(
        userId: String,
        date: String,
        interviewType: InterviewType,
        answers: List<InterviewAnswer>
    ): Flow<DataResource<Unit>>

    suspend fun saveSleepData(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        filePath: String
    )

    fun getInterviewQuestionList(interviewType: InterviewType): Flow<DataResource<List<InterviewQuestion>>>

    fun sendInterviewMessage(
        userId: String,
        message: String
    ): Flow<DataResource<String?>>

    fun validateAccount(userId: String): Flow<DataResource<Unit>>

    fun saveUserId(userId: String): Flow<DataResource<Unit>>

    fun getUserId(): Flow<DataResource<String?>>
}
