package ted.gun0912.sleep.data.bound

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import ted.gun0912.sleep.dataResource.DataResource

class FlowPersistableRemoteBoundResource<T>(
    dataAction: suspend () -> T,
    private val localDataAction: suspend () -> T?,
    private val saveCacheAction: suspend (T) -> Unit,
) : FlowBaseBoundResource<T>(dataAction) {

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<DataResource<T>>) {
        try {
            val localData: T? =
                try {
                    localDataAction()
                } catch (exception: Exception) {
                    null
                }
            collector.emit(DataResource.loading(localData))
            fetchFromSource(collector, saveCacheAction)
        } catch (exception: Exception) {
            collector.emit(DataResource.error(exception))
        }
    }

}
