package ted.gun0912.sleep.model

data class ADCDataInfo(
    override val rawText: String,
    val ADCData1: String,
    val ADCData2: String,
) : SensorInfo(rawText) {

    companion object {
        fun from(rawText: String): ADCDataInfo? {

            val result = rawText.split(",")

            if (result.size != 2) {
                return null
            }

            val adcData1 = result[0].trim()
            val adcData2 = result[1].trim()

            return ADCDataInfo(rawText, adcData1, adcData2)
        }
    }
}
