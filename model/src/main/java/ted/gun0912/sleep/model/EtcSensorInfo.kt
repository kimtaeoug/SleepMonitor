package ted.gun0912.sleep.model

data class EtcSensorInfo(
    override val rawText: String,
    val adcPower: Int,
    val motionPower: Int,
    val RRPower: Int,
    val HRPower: Int,
) : SensorInfo(rawText) {

    companion object {
        fun from(rawText: String): EtcSensorInfo? {
            if (!rawText.startsWith(PREFIX_ETC_SENSOR_TEXT)) {
                return null
            }
            val result = rawText.split(",")
            if (result.size < 9) {
                return null
            }
            val adcPower = result[2].trim().toInt()
            val motionPower = result[3].trim().toInt()
            val RRPower = result[6].trim().toInt()
            val HRPower = result[9].trim().toInt()

            return EtcSensorInfo(rawText, adcPower, motionPower, RRPower, HRPower)
        }
    }
}
