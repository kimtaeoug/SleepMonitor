package ted.gun0912.sleep.bluetooth.bluetooth_classic

import android.bluetooth.BluetoothSocket
import android.util.Log
import ted.gun0912.sleep.component.model.BluetoothResult
import ted.gun0912.sleep.model.EnvSensorInfo
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class SocketDataManagerThread(
    private val bluetoothSocket: BluetoothSocket,
    private val onDataChange: (BluetoothResult?) -> Unit
) : Thread() {
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    init {
        try {
            inputStream = bluetoothSocket.inputStream
            outputStream = bluetoothSocket.outputStream
        } catch (e: IOException) {
            // setLog(TAG, e.message.toString())
            Log.e("ManageSocketDataThread", e.message.toString())
        }
    }

    override fun run() {
        //   val buffer = ByteArray(1024)
        //   var bytes: Int
        val buffer = ByteArray(512)
        var bytes: Int
        val readMessage = StringBuilder()
        while (true) {
            try {
                bytes = inputStream.read(buffer)
                val readed = String(buffer, 0, bytes)
                readMessage.append(readed)

                if (readed.contains("\n")) {
                    readMessage.setLength(0)
                }
                val envSensorInfo = parseEnvSensorMessage(readed)
                Log.e("data", readed)
                onDataChange(envSensorInfo?.let { BluetoothResult.Data(it) })
            } catch (e: IOException) {
                onDataChange(BluetoothResult.Disconnected)
                e.printStackTrace()
                Log.e("ManageSocketDataThread", "IOException")
                break
            } catch (e: Exception) {
                Log.e("ManageSocketDataThread", "Exception")
                break
            }
        }
    }

    fun write(bytes: ByteArray) {
        try {
            // 데이터 전송
            outputStream.write(bytes)
        } catch (e: IOException) {
            //  setLog(TAG, e.message.toString())
        }
    }

    fun cancel() {
        try {
            bluetoothSocket.close()
        } catch (e: IOException) {
            //   setLog(TAG, e.message.toString())
        }
    }
}

private fun parseEnvSensorMessage(message: String): EnvSensorInfo? {
    return if (message.startsWith("data:")) {
        try {
            val fields = message.split(":")
            val tvocStr = fields[1]
            val co2Str = fields[2]
            val tempStr = fields[3]
            val humidityStr = fields[4]
            val tvocIdxStr = fields[5].replace("(\r\n|\r|\n|\n\r)".toRegex(), "")
            Log.e("fields", fields.toString())
            EnvSensorInfo(
                tvocStr,
                co2Str,
                tempStr,
                humidityStr,
                tvocIdxStr
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } else {
        null
    }
}


