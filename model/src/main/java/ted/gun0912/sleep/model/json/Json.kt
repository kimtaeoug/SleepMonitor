package ted.gun0912.sleep.model.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import ted.gun0912.sleep.model.json.serializer.LocalDateJsonSerializer
import ted.gun0912.sleep.model.json.serializer.LocalDateTimeJsonSerializer
import ted.gun0912.sleep.model.json.serializer.LocalTimeJsonSerializer


private val module = SerializersModule {
    contextual(LocalDateTimeJsonSerializer)
    contextual(LocalDateJsonSerializer)
    contextual(LocalTimeJsonSerializer)
}

val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
    serializersModule = module

}
