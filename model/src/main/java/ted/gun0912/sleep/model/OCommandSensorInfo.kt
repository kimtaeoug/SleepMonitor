package ted.gun0912.sleep.model

data class OCommandSensorInfo(
    override val rawText: String,
    val hrPThr : Int,
    val brPThr : Int,
) : SensorInfo(rawText) {

    companion object {
        fun from(rawText: String): OCommandSensorInfo? {
            if (!rawText.startsWith(PREFIX_O_COMMAND_SENSOR_TEXT)) {
                return null
            }
            val result = rawText.split(",")
            if (result.size < 5) {
                return null
            }

            val hrPThr = result[2].trim().toInt()
            val brPThr = result[3].trim().toInt()

            return OCommandSensorInfo(rawText,hrPThr,brPThr)
        }
    }
}
