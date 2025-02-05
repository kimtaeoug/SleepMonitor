package ted.gun0912.sleep.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ted.gun0912.sleep.local.constant.RoomConstant
import ted.gun0912.sleep.local.model.SleepRecord
import ted.gun0912.sleep.local.room.converter.DtoConverter
import ted.gun0912.sleep.local.room.dao.SleepMonitorDao

@Database(
    entities = [SleepRecord::class],
    version = RoomConstant.ROOM_VERSION
)
@TypeConverters(
    DtoConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sleepMonitorDao(): SleepMonitorDao

}
