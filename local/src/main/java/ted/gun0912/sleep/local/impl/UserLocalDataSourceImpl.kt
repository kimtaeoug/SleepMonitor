package ted.gun0912.sleep.local.impl

import ted.gun0912.sleep.data.local.UserLocalDataResource
import ted.gun0912.sleep.local.pref.PrefUtil
import ted.gun0912.sleep.model.User
import javax.inject.Inject

class UserLocalDataSourceImpl @Inject constructor(
    private val prefUtil: PrefUtil,
) : UserLocalDataResource {
    override suspend fun getUser(): User? = prefUtil.user

    override suspend fun setUser(user: User) {
        prefUtil.user = user
    }

    override suspend fun saveUserId(userId: String) {
        prefUtil.userId = userId
    }

    override suspend fun getUserId(): String? = prefUtil.userId

}
