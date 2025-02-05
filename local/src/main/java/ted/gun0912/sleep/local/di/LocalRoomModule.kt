package ted.gun0912.sleep.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ted.gun0912.sleep.local.constant.RoomConstant
import ted.gun0912.sleep.local.room.AppDatabase
import ted.gun0912.sleep.local.room.dao.SleepMonitorDao
import ted.gun0912.sleep.local.room.migration.roomMigrations
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalRoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            RoomConstant.ROOM_DB_NAME
        )
            .fallbackToDestructiveMigration()
            .addMigrations(*roomMigrations)
            .build()

    @Provides
    @Singleton
    fun provideSleepMonitorDao(database: AppDatabase): SleepMonitorDao = database.sleepMonitorDao()
}
