package ted.gun0912.sleep.common.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ted.gun0912.sleep.common.exception.ErrorHandlerImpl
import ted.gun0912.sleep.component.ErrorHandler
import ted.gun0912.sleep.component.VersionName

import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal abstract class CommonModule {

    @Binds
    @Singleton
    abstract fun bindErrorHandler(errorHandler: ErrorHandlerImpl): ErrorHandler

}
