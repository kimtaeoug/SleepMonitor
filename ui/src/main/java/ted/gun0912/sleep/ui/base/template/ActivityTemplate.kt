package ted.gun0912.sleep.ui.base.template

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.gun0912.tedonactivityresult.TedOnActivityResult
import com.gun0912.tedonactivityresult.model.ActivityResult
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
abstract class ActivityTemplate<out A : Activity> {

    fun startActivity(
        context: Context,
        vararg extras: Pair<String, Any?>,
        intentAction: Intent.() -> Unit = {}
    ) {
        val intent = getIntent(context, *extras, intentAction = intentAction)
        context.startActivity(intent)
    }

    fun startActivityForResult(
        context: Context,
        vararg extras: Pair<String, Any?>,
        intentAction: Intent.() -> Unit = {},
        subscribeAction: (Intent?) -> Unit
    ) {
        startActivityForResultAlways(
            context,
            *extras,
            intentAction = intentAction,
            subscribeAction = { activityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    subscribeAction(activityResult.data)
                }
            })
    }

    fun startActivityForResultAlways(
        context: Context,
        vararg extras: Pair<String, Any?>,
        intentAction: Intent.() -> Unit = {},
        subscribeAction: (ActivityResult) -> Unit,
    ) {
        val intent = getIntent(context, *extras, intentAction = intentAction)
        TedOnActivityResult.with(context)
            .setIntent(intent)
            .setListener { resultCode, data ->
                subscribeAction(ActivityResult(resultCode, data))
            }
            .startActivityForResult()

    }

    fun getIntent(
        context: Context,
        vararg extras: Pair<String, Any?>,
        intentAction: Intent.() -> Unit = {}
    ): Intent {
        val genericType =
            (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val clz: Class<A> =
            if (genericType is ParameterizedType) {
                genericType.rawType as Class<A>
            } else {
                genericType as Class<A>
            }
        return Intent(context, clz).apply {
            putExtras(bundleOf(*extras))
            intentAction()
        }
    }

}
