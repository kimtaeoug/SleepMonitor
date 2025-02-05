package ted.gun0912.sleep.model

enum class VitalSensorState(val value: Int) {
    NONE(0),
    LOW_POWER1(1),
    LOW_POWER2(2),
    NORMAL(3),
    MOVING(4);

    val isStable: Boolean
        get() = when (this) {
            NONE, MOVING -> false
            LOW_POWER1, LOW_POWER2, NORMAL -> true
        }

    val isNotStable: Boolean
        get() = !isStable

    companion object {
        fun find(value: Int): VitalSensorState = values().find { value == it.value } ?: NONE
    }
}
