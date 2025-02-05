package ted.gun0912.sleep.data.local

import ted.gun0912.sleep.model.User

interface UserLocalDataResource {

    suspend fun getUser(): User?

    suspend fun setUser(user: User)

    suspend fun saveUserId(userId: String): Unit

    suspend fun getUserId(): String?
}
