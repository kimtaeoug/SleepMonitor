package ted.gun0912.sleep.model.json.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object LocalTimeJsonSerializer : KSerializer<LocalTime> {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    
    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(dateTimeFormatter.format(value))
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        val localTimeText = decoder.decodeString()
        return LocalTime.parse(localTimeText, dateTimeFormatter)
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

}
