package ted.gun0912.sleep.ui.permission

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.gun0912.tedonactivityresult.TedOnActivityResult
import com.gun0912.tedpermission.TedPermissionUtil
import com.gun0912.tedpermission.coroutine.TedPermission
import ted.gun0912.sleep.ui.R
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object PermissionUtil {

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun hasPermission(context: Context): Boolean =
        TedPermissionUtil.isGranted(*permissions.toTypedArray())
            && areBluetoothEnabled(context)
            && areBatteryOptimizationEnabled(context)
            && canScheduleExactAlarms(context)

    suspend fun requestPermission(activity: FragmentActivity): Boolean {
        val requestPermissionResult = TedPermission.create()
            .setPermissions(*permissions.toTypedArray())
            .setDeniedMessage(R.string.permission_deny_message)
            .checkGranted()
        if (!requestPermissionResult) {
            return false
        }
        val requestBluetoothEnableResult = requestBluetoothEnable(activity)
        if (!requestBluetoothEnableResult) {
            return false
        }

        val requestBatteryOptimizationResult = requestBatteryOptimizationEnable(activity)
        if (!requestBatteryOptimizationResult) {
            return false
        }

        val requestScheduleExactAlarmsResult = requestScheduleExactAlarms(activity)
        if (!requestScheduleExactAlarmsResult) {
            return false
        }

        return true
    }

    private suspend fun requestBluetoothEnable(activity: FragmentActivity): Boolean =
        suspendCoroutine { continuation ->
            if (areBluetoothEnabled(activity)) {
                continuation.resume(true)
                return@suspendCoroutine
            }
            val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            TedOnActivityResult.with(activity)
                .setIntent(bluetoothIntent)
                .setListener { resultCode, data ->
                    continuation.resume(resultCode == Activity.RESULT_OK)
                }
                .startActivityForResult()
        }


    private fun areBluetoothEnabled(context: Context): Boolean {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter.isEnabled
    }

    private fun areBatteryOptimizationEnabled(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    private suspend fun requestBatteryOptimizationEnable(activity: FragmentActivity): Boolean =
        suspendCoroutine { continuation ->
            if (areBatteryOptimizationEnabled(activity)) {
                continuation.resume(true)
                return@suspendCoroutine
            }
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                data = Uri.parse("package:" + activity.packageName)
            }
            TedOnActivityResult.with(activity)
                .setIntent(intent)
                .setListener { _, _ ->
                    continuation.resume(areBatteryOptimizationEnabled(activity))
                }
                .startActivityForResult()
        }

    private fun canScheduleExactAlarms(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager: AlarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

    private suspend fun requestScheduleExactAlarms(activity: FragmentActivity): Boolean =
        suspendCoroutine { continuation ->
            if (canScheduleExactAlarms(activity)) {
                continuation.resume(true)
                return@suspendCoroutine
            }
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                data = Uri.parse("package:" + activity.packageName)
            }
            TedOnActivityResult.with(activity)
                .setIntent(intent)
                .setListener { _, _ ->
                    continuation.resume(canScheduleExactAlarms(activity))
                }
                .startActivityForResult()
        }
}
