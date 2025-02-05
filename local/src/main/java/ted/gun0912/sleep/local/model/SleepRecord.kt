package ted.gun0912.sleep.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ted.gun0912.sleep.local.constant.RoomConstant
import ted.gun0912.sleep.model.SleepType
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = RoomConstant.Table.SLEEP_RECORD)
data class SleepRecord(
    @PrimaryKey
    @ColumnInfo(name = "recordDay")
    val recordDay: LocalDate,
    @ColumnInfo(name = "sleepType")
    val sleepType: SleepType,
    @ColumnInfo(name = "sleepTime")
    val sleepTime: LocalDateTime,
    @ColumnInfo(name = "wakeUpTime")
    val wakeUpTime: LocalDateTime,
    @ColumnInfo(name = "rawText")
    val rawText: String
)
