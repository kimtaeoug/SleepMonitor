package ted.gun0912.sleep.data.bound

fun <T> flowDataResource(dataAction: suspend () -> T) = FlowBoundResource(dataAction)

fun <T> flowDataResource(
    dataAction: suspend () -> T,
    localSourceAction: suspend () -> T?,
    saveCache: suspend (T) -> Unit,
) =
    FlowPersistableRemoteBoundResource(dataAction, localSourceAction, saveCache)
