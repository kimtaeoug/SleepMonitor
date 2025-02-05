package ted.gun0912.sleep.data.impl

import kotlinx.coroutines.flow.Flow
import ted.gun0912.sleep.data.bound.flowDataResource
import ted.gun0912.sleep.data.local.UserLocalDataResource
import ted.gun0912.sleep.data.remote.UserRemoteDataSource
import ted.gun0912.sleep.dataResource.DataResource
import ted.gun0912.sleep.domain.repository.UserRepository
import ted.gun0912.sleep.model.Interview
import ted.gun0912.sleep.model.InterviewAnswer
import ted.gun0912.sleep.model.InterviewQuestion
import ted.gun0912.sleep.model.InterviewType
import ted.gun0912.sleep.model.SensorInfo
import ted.gun0912.sleep.model.User
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userLocalDataResource: UserLocalDataResource,
    private val userRemoteDataResource: UserRemoteDataSource
) : UserRepository {
    override fun getUser(): Flow<DataResource<User?>> = flowDataResource {
        userLocalDataResource.getUser()
    }

    override fun updateUser(user: User): Flow<DataResource<Unit>> = flowDataResource {
        userLocalDataResource.setUser(user)
    }

    override fun getInterviews(userId: String): Flow<DataResource<List<Interview>>> = flowDataResource {
        userRemoteDataResource.getInterviews(userId)
    }

    override fun saveInterviewAnswerList(
        userId: String,
        date: String,
        interviewType: InterviewType,
        answers: List<InterviewAnswer>
    ): Flow<DataResource<Unit>> = flowDataResource {
        userRemoteDataResource.updateInterviewAnswers(userId, date, interviewType, answers)
    }

    override suspend fun saveSleepData(
        userId: String,
        sleepTime: LocalDateTime,
        wakeUpTime: LocalDateTime,
        filePath: String
    ) {
        userRemoteDataResource.saveSleepRecords(
            userId = userId,
            sleepTime = sleepTime,
            wakeUpTime = wakeUpTime,
            filePath = filePath
        )
    }

    override fun getInterviewQuestionList(
        interviewType: InterviewType
    ): Flow<DataResource<List<InterviewQuestion>>> =
        flowDataResource {
            userRemoteDataResource.getInterviewQuestions(interviewType)
        }

    override fun sendInterviewMessage(
        userId: String,
        message: String
    ): Flow<DataResource<String?>> = flowDataResource {
        userRemoteDataResource.sendMessage(userId, message)
    }

    override fun validateAccount(
        userId: String
    ): Flow<DataResource<Unit>> = flowDataResource {
        userRemoteDataResource.validateUser(userId)
    }

    override fun saveUserId(userId: String): Flow<DataResource<Unit>> = flowDataResource {
        userLocalDataResource.saveUserId(userId)
    }

    override fun getUserId(): Flow<DataResource<String?>> = flowDataResource {
        userLocalDataResource.getUserId()
    }
}
