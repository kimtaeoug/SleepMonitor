package ted.gun0912.sleep.model

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ted.gun0912.sleep.model.json.fromJson
import ted.gun0912.sleep.model.json.toJson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class JsonExtensionTest {

    @DisplayName("`1986-09-12 18:00:00`를 LocalDateTime으로 변환할 수 있어야 한다")
    @Test
    fun localDateTime1() {
        // given
        val jsonText = JsonPrimitive("1986-09-12 18:00:00").toString()

        // when
        val actual = jsonText.fromJson<LocalDateTime>()

        // then
        val expected = LocalDateTime.of(1986, 9, 12, 18, 0, 0)
        assertThat(actual).isEqualTo(expected)
    }

    @DisplayName("`1986-09-12`를 LocalDateTime으로 변환할 수 있어야 한다")
    @Test
    fun localDateTime3() {
        // given
        val jsonText = JsonPrimitive("1986-09-12").toString()

        // when
        val actual = jsonText.fromJson<LocalDateTime>()

        // then
        val expected = LocalDateTime.of(1986, 9, 12, 0, 0, 0)
        assertThat(actual).isEqualTo(expected)
    }

    @DisplayName("LocalDateTime으로 변환할 수 없으면 null이 나와야 한다")
    @Test
    fun localDateTime2() {
        // given
        val jsonText = JsonPrimitive("1986-09-12 12:").toString()

        // when
        val actual = jsonText.fromJson<LocalDateTime>()

        // then
        assertThat(actual).isNull()
    }

    @DisplayName("`1986-09-12`를 LocalDate로 변환할 수 있어야 한다")
    @Test
    fun localDate1() {
        // given
        val jsonText = JsonPrimitive("1986-09-12").toString()

        // when
        val actual = jsonText.fromJson<LocalDate>()

        // then
        val expected = LocalDate.of(1986, 9, 12)
        assertThat(actual).isEqualTo(expected)
    }

    @DisplayName("LocalDate를 `1986-09-12` 로 변환할 수 있어야한다")
    @Test
    fun serializeLocalDate() {
        // given
        val localDate = LocalDate.of(1986, 9, 12)

        // when
        val actual = localDate.toJson()

        // then
        val expected = JsonPrimitive("1986-09-12").toString()
        assertThat(actual).isEqualTo(expected)
    }


    @DisplayName("`09:12:00`를 LocalTime으로 변환할 수 있어야 한다")
    @Test
    fun localTime1() {
        // given
        val jsonText = JsonPrimitive("09:12:00").toString()

        // when
        val actual = jsonText.fromJson<LocalTime>()

        // then
        val expected = LocalTime.of(9, 12, 0)
        assertThat(actual).isEqualTo(expected)
    }

    @DisplayName("`18:15:05`를 LocalTime으로 변환할 수 있어야 한다")
    @Test
    fun localTime2() {
        // given
        val jsonText = JsonPrimitive("18:15:05").toString()

        // when
        val actual = jsonText.fromJson<LocalTime>()

        // then
        val expected = LocalTime.of(18, 15, 5)
        assertThat(actual).isEqualTo(expected)
    }

    @DisplayName("LocalTime을 `09:12:00` 로 변환할 수 있어야한다")
    @Test
    fun serializeLocalTime() {
        // given
        val localDate = LocalTime.of(9, 12, 0)

        // when
        val actual = localDate.toJson()

        // then
        val expected = JsonPrimitive("09:12:00").toString()
        assertThat(actual).isEqualTo(expected)
    }
}
