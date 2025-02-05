package ted.gun0912.sleep.model

data class VitalSensorInfo(
    override val rawText: String,
    val heartRate: Int,
    val respirationRate: Int,
    val state: VitalSensorState,
    val stateValue : Int,
    val instantHR: Int,
    val peakHR: Int,
    val fftHR: Int,
    val adcIn: Int,
    val adcIn2: Int,
) : SensorInfo(rawText) {

    companion object {

        fun from(rawText: String): VitalSensorInfo? {
            if (!rawText.startsWith(PREFIX_VITAL_SENSOR_TEXT)) {
                return null
            }
            val result = rawText.split(",")
            if (result.size < 9) {
                return null
            }

            val heartRate = result[1].trim().toInt()
            val respirationRate = result[2].trim().toInt()
            val stateValue = result[3].trim().toInt()
            val state = VitalSensorState.find(stateValue)
            val instantHR = result[4].trim().toInt()
            val peakHR = result[5].trim().toInt()
            val fftHR = result[6].trim().toInt()
            val adcIn1 = result[7].trim().toInt()
            val adcIn2 = result[8].trim().toInt()

            return VitalSensorInfo(
                rawText,
                heartRate,
                respirationRate,
                state,
                stateValue,
                instantHR,
                peakHR,
                fftHR,
                adcIn1,
                adcIn2
            )
        }
    }
}
