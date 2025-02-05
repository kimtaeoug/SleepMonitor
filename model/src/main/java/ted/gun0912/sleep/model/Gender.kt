package ted.gun0912.sleep.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Gender {
    @SerialName("남자")
    MAN,

    @SerialName("여자")
    WOMAN;
}
