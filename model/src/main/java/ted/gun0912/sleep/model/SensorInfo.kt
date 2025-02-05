package ted.gun0912.sleep.model

sealed class SensorInfo(open val rawText: String) {

    companion object {
        const val PREFIX_ETC_SENSOR_TEXT = "R"
        const val PREFIX_VITAL_SENSOR_TEXT = "H"
        const val PREFIX_D_COMMAND_SENSOR_TEXT = "D"
        const val PREFIX_O_COMMAND_SENSOR_TEXT = "O"

        fun from(rawText: String): SensorInfo? = when {
            rawText.startsWith(PREFIX_VITAL_SENSOR_TEXT) -> VitalSensorInfo.from(rawText)
            rawText.startsWith(PREFIX_ETC_SENSOR_TEXT) -> EtcSensorInfo.from(rawText)
            rawText.startsWith(PREFIX_D_COMMAND_SENSOR_TEXT) -> DcommandSensorInfo.from(rawText)
            rawText.startsWith(PREFIX_O_COMMAND_SENSOR_TEXT) -> OCommandSensorInfo.from(rawText)
            //else -> ADCDataInfo.from(rawText) //YJH0627 ADC데이터 저장
            else -> null
        }
    }
}

fun List<SensorInfo>.partition(): Pair<List<VitalSensorInfo>, List<EtcSensorInfo>> {
    val vitalList = mutableListOf<VitalSensorInfo>()
    val etcSensorList = mutableListOf<EtcSensorInfo>()
    forEach {
        when (it) {
            is VitalSensorInfo -> vitalList.add(it)
            is EtcSensorInfo -> etcSensorList.add(it)
        }
    }
    return vitalList to etcSensorList
}
