package ted.gun0912.sleep.bluetooth.bluetooth_classic

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import ted.gun0912.sleep.component.model.BluetoothResult
import ted.gun0912.sleep.model.EnvSensorInfo
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.UUID


@SuppressLint("MissingPermission")
class ConnectionThread(
    private val device: BluetoothDevice,
    private val onDataChange: (BluetoothResult?) -> Unit,
) : Thread() {
    companion object {
        private const val TAG = "CONNECT_THREAD"
    }

    private var socketThread: SocketDataManagerThread? = null

    // BluetoothDevice 로부터 BluetoothSocket 획득
    private val connectSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        createRfcommSocket(device)
    }

    override fun run() {
        try {
            // 연결 수행
            connectSocket!!.connect()
            connectSocket?.let {
                socketThread =
                    SocketDataManagerThread(bluetoothSocket = it, onDataChange = onDataChange)
                socketThread!!.start()
            }
            onDataChange(BluetoothResult.Connected)
        } catch (e: IOException) { // 기기와의 연결이 실패할 경우 호출
            Log.e("connectSocket-error", e.message.toString())
            connectSocket?.close()
            onDataChange(BluetoothResult.Disconnected)
        }
    }


    fun cancel() {
        try {
            connectSocket?.close()
            socketThread?.cancel()
            socketThread = null
            onDataChange(BluetoothResult.Disconnected)

        } catch (e: IOException) {
            //  setLog(TAG, e.message.toString())
            Log.e("ConnectionThread", e.message.toString())
        }
    }

    private fun createRfcommSocket(device: BluetoothDevice): BluetoothSocket? {
        var tmp: BluetoothSocket? = null
        try {
            val class1 = device.javaClass
            val aclass = arrayOf(Integer.TYPE)
            val method = class1.getMethod("createRfcommSocket", *aclass)
            val aobj = arrayOf(1)
            tmp = method.invoke(device, *aobj) as BluetoothSocket
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            Log.e(TAG, "createRfcommSocket() failed", e)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            Log.e(TAG, "createRfcommSocket() failed", e)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            Log.e(TAG, "createRfcommSocket() failed", e)
        }
        return tmp
    }

}
