package ted.gun0912.sleep.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ted.gun0912.sleep.data.impl.ConfigRepositoryImpl
import ted.gun0912.sleep.data.impl.StatisticRepositoryImpl
import ted.gun0912.sleep.data.impl.UserRepositoryImpl
import ted.gun0912.sleep.domain.repository.ConfigRepository
import ted.gun0912.sleep.domain.repository.StatisticRepository
import ted.gun0912.sleep.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindConfigRepository(repo: ConfigRepositoryImpl): ConfigRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(repo: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindStatisticRepository(repo: StatisticRepositoryImpl): StatisticRepository

}
