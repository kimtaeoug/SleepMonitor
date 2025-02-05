package ted.gun0912.sleep.device.extension

import android.content.Context

fun Context.getVersionName(): String = runCatching {
    packageManager.getPackageInfo(packageName, 0).versionName
}.getOrDefault("")
