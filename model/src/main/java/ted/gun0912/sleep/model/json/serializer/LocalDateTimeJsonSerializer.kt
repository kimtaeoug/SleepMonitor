package ted.gun0912.sleep.model.json.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeJsonSerializer : KSerializer<LocalDateTime> {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(dateTimeFormatter.format(value))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val localDateTimeText = decoder.decodeString()
        return runCatching { LocalDateTime.parse(localDateTimeText, dateTimeFormatter) }
            .recoverCatching { LocalDate.parse(localDateTimeText).atStartOfDay() }
            .getOrThrow()
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

}
