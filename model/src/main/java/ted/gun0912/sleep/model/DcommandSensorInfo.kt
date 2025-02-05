package ted.gun0912.sleep.model

data class DcommandSensorInfo(
    override val rawText: String,
    val motionThreshold: Int,
    val movPowerThreshold: Int,
) : SensorInfo(rawText) {

    companion object {
        fun from(rawText: String): DcommandSensorInfo? {
            if (!rawText.startsWith(PREFIX_D_COMMAND_SENSOR_TEXT)) {
                return null
            }
            val result = rawText.split(",")
            if (result.size < 6) {
                return null
            }
            val motionThreshold = result[2].trim().toInt()
            val movPowerThreshold = result[5].trim().toInt()

            return DcommandSensorInfo(rawText, motionThreshold, movPowerThreshold)
        }
    }
}
