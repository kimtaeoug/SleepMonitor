
package ted.gun0912.sleep.ui

import androidx.compose.runtime.mutableStateOf
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class LoggerUtil(logTag: String) {
    private var _tag = mutableStateOf("TAG_LOGGER")
    fun setTag(value : String){
        _tag.value = value
    }
    fun getInstance(): LoggerUtil {
        Logger.addLogAdapter(AndroidLogAdapter())
        return this
    }

    fun debug(s: String): LoggerUtil {
        Logger.t(_tag.value).d(s)
        return this
    }

    fun info(s: String): LoggerUtil {
        Logger.t(_tag.value).i(s)
        return this
    }

    fun warning(s: String): LoggerUtil {
        Logger.t(_tag.value).w(s)
        return this
    }

    fun error(s: String): LoggerUtil {
        Logger.t(_tag.value).e(s)
        return this
    }
    init {
        setTag(logTag)
    }


}