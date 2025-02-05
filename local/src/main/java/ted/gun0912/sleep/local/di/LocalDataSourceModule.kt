package ted.gun0912.sleep.local.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ted.gun0912.sleep.data.local.ConfigLocalDataResource
import ted.gun0912.sleep.data.local.StatisticLocalDataResource
import ted.gun0912.sleep.data.local.UserLocalDataResource
import ted.gun0912.sleep.local.impl.ConfigLocalDataSourceImpl
import ted.gun0912.sleep.local.impl.StatisticLocalDataSourceImpl
import ted.gun0912.sleep.local.impl.UserLocalDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindConfigLocalDataResource(source: ConfigLocalDataSourceImpl): ConfigLocalDataResource

    @Binds
    @Singleton
    abstract fun bindUserLocalDataResource(source: UserLocalDataSourceImpl): UserLocalDataResource

    @Binds
    @Singleton
    abstract fun bindStatisticLocalDataResource(source: StatisticLocalDataSourceImpl): StatisticLocalDataResource
}
