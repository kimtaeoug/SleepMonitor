package ted.gun0912.sleep.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ted.gun0912.sleep.local.constant.RoomConstant
import ted.gun0912.sleep.local.model.SleepRecord
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate

@Dao
interface SleepMonitorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleepRecord: SleepRecord)

    @Query("SELECT recordDay FROM ${RoomConstant.Table.SLEEP_RECORD} WHERE sleepType=:sleepType")
    suspend fun getSleepRecordDates(sleepType: SleepType): List<LocalDate>

    @Query("SELECT * FROM ${RoomConstant.Table.SLEEP_RECORD} WHERE sleepType=:sleepType AND recordDay = :recordDay")
    suspend fun getSleepRecord(sleepType: SleepType, recordDay: LocalDate): SleepRecord?

    @Query("SELECT * FROM ${RoomConstant.Table.SLEEP_RECORD} WHERE sleepType=:sleepType AND recordDay BETWEEN :startDate AND :endDate")
    suspend fun getSleepRecords(
        sleepType: SleepType,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<SleepRecord>

    @Query("SELECT * FROM ${RoomConstant.Table.SLEEP_RECORD} WHERE sleepType=:sleepType LIMIT 1")
    suspend fun getFirstSleepRecord(sleepType: SleepType): SleepRecord?

}
