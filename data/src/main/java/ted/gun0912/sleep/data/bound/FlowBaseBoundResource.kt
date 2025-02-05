package ted.gun0912.sleep.data.bound

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import ted.gun0912.sleep.dataResource.DataResource

abstract class FlowBaseBoundResource<T>(private val dataAction: suspend () -> T) :
    Flow<DataResource<T>> {

    protected open suspend fun fetchFromSource(
        collector: FlowCollector<DataResource<T>>,
        successAction: (suspend (T) -> Unit)? = null,
    ) {
        try {
            val data = dataAction()
            collector.emit(DataResource.success(data))
            successAction?.invoke(data)
        } catch (exception: Exception) {
            collector.emit(DataResource.error(exception))
        }
    }

}
