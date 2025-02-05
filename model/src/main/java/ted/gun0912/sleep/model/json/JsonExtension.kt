package ted.gun0912.sleep.model.json

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

inline fun <reified T> T.toJson(): String =
    json.encodeToString(this)

inline fun <reified T> String.fromJson(): T? =
    json.decodeFromString<T>(this)

