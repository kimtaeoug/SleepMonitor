package ted.gun0912.sleep.local.pref

import android.app.Application
import ted.gun0912.sleep.model.BluetoothDevice
import ted.gun0912.sleep.model.User
import ted.gun0912.sleep.model.json.fromJson
import ted.gun0912.sleep.model.json.toJson
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefUtil @Inject constructor(application: Application) {

    private val sharedPreference = SharedPreference(application)

    var bluetoothDevice: BluetoothDevice
        get() = sharedPreference.get(PREF_DEVICE) ?: BluetoothDevice("", "")
        set(value) {
            updateOrAddDevice(value)
            sharedPreference.put(PREF_DEVICE, value)
        }

    var envBluetoothDevice: BluetoothDevice
        get() = sharedPreference.get(PREF_ENV_DEVICE) ?: BluetoothDevice("", "")
        set(value) {
            updateOrAddDevice(value)
            sharedPreference.put(PREF_ENV_DEVICE, value)
        }

    var needShowVitalInfo: Boolean
        get() = sharedPreference.get(PREF_NEED_SHOW_VITAL_INFO, true)
        set(value) {
            sharedPreference.put(PREF_NEED_SHOW_VITAL_INFO, value)
        }

    var needShowChargePhoneNotice: Boolean
        get() = sharedPreference.get(PREF_NEED_SHOW_CHARGE_PHONE_NOTICE, true)
        set(value) {
            sharedPreference.put(PREF_NEED_SHOW_CHARGE_PHONE_NOTICE, value)
        }

    var lastAlarmTime: LocalTime
        get() {
            val second: Int = sharedPreference.get(PREF_LAST_ALARM_TIME, -1)
            return if (second < 0) {
                LocalTime.now()
            } else {
                LocalTime.ofSecondOfDay(second.toLong())
            }

        }
        set(localTime) {
            sharedPreference.put(PREF_LAST_ALARM_TIME, localTime.toSecondOfDay())
        }

    var user: User?
        get() = sharedPreference.get(PREF_USER)
        set(user) {
            user ?: return
            sharedPreference.put(PREF_USER, user)
        }

    var userId: String?
        get() = sharedPreference.get(PREF_USER_ID)
        set(userId) {
            userId ?: return
            sharedPreference.put(PREF_USER_ID, userId)
        }
    var movthr: String
        get() = sharedPreference.get(PREF_MOVTHR, "")
        set(value) {
            sharedPreference.put(PREF_MOVTHR, value)
        }

    var motthr: String
        get() = sharedPreference.get(PREF_MOTTHR, "")
        set(value) {
            sharedPreference.put(PREF_MOTTHR, value)
        }

    private fun updateOrAddDevice(device: BluetoothDevice) {
        val devices = bluetoothDevices.toMutableList()
        val index = devices.indexOfFirst { it.address == device.address }
        if (index >= 0) {
            devices[index] = device
        } else {
            devices.add(device)
        }
        bluetoothDevices = devices
    }

    var bluetoothDevices: List<BluetoothDevice>
        get() {
            val bluetoothDeviceSet: Set<String> =
                sharedPreference.get(PREF_BLUETOOTH_DEVICE_SET, emptySet())
            return bluetoothDeviceSet.mapNotNull { it.fromJson() }
        }
        set(value) {
            val bluetoothDeviceSet = value.map { it.toJson() }.toSet()
            sharedPreference.put(PREF_BLUETOOTH_DEVICE_SET, bluetoothDeviceSet)
        }

    companion object {
        private const val PREF_DEVICE = "PREF_DEVICE"
        private const val PREF_ENV_DEVICE = "PREF_ENV_DEVICE"
        private const val PREF_BLUETOOTH_DEVICE_SET = "PREF_BLUETOOTH_DEVICE_SET"
        private const val PREF_NEED_SHOW_VITAL_INFO = "PREF_NEED_SHOW_VITAL_INFO"
        private const val PREF_NEED_SHOW_CHARGE_PHONE_NOTICE = "PREF_NEED_SHOW_CHARGE_PHONE_NOTICE"
        private const val PREF_LAST_ALARM_TIME = "PREF_LAST_ALARM_TIME"

        private const val PREF_USER = "PREF_USER"

        private const val PREF_USER_ID = "PREF_USER_ID"

        private const val PREF_MOVTHR = "PREF_MOVTHR"
        private const val PREF_MOTTHR = "PREF_MOTTHR"
    }
}
