package ted.gun0912.sleep.common.util

import java.util.UUID

object Constant {
    val ENV_RX_SERVICE_UUID_TEXT = "0000ffe0-0000-1000-8000-00805f9b34fb"

    val RX_SERVICE_UUID_TEXT = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
    val RX_SERVICE_UUID = UUID.fromString(RX_SERVICE_UUID_TEXT)
    val RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    val TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
}
