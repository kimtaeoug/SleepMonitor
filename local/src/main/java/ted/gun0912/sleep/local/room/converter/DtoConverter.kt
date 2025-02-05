package ted.gun0912.sleep.local.room.converter

import androidx.room.TypeConverter
import ted.gun0912.sleep.model.extension.DateConverter
import ted.gun0912.sleep.model.extension.toSecond
import java.time.LocalDate
import java.time.LocalDateTime

class DtoConverter {

    @TypeConverter
    fun localDateToSecond(localDate: LocalDate?): Long? = localDate?.toSecond()

    @TypeConverter
    fun secondToLocalDate(second: Long?): LocalDate? =
        second?.let { DateConverter.secondToLocalDate(second) }


    @TypeConverter
    fun localDateToSecond(localDateTime: LocalDateTime?): Long? = localDateTime?.toSecond()

    @TypeConverter
    fun secondToLocalDateTime(second: Long?): LocalDateTime? =
        second?.let { DateConverter.secondToLocalDateTime(second) }

}
