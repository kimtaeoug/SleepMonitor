package ted.gun0912.sleep.data.bound

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import ted.gun0912.sleep.dataResource.DataResource

class FlowBoundResource<T>(dataAction: suspend () -> T) : FlowBaseBoundResource<T>(dataAction) {

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<DataResource<T>>) {
        collector.emit(DataResource.loading<T>() as DataResource<T>)
        fetchFromSource(collector)
    }

}
