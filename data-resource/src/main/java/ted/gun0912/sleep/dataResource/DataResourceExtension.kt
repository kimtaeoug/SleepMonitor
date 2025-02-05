package ted.gun0912.sleep.dataResource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform

inline fun <T> buildDataResource(builderAction: () -> T) = try {
    DataResource.success(builderAction())
} catch (t: Throwable) {
    DataResource.error(t)
}

inline fun <T, R> DataResource<T>.map(
    transform: (data: T) -> R,
): DataResource<R> = try {
    when (this) {
        is DataResource.Success -> DataResource.success(transform(data))
        is DataResource.Error -> DataResource.error(throwable)
        is DataResource.Loading -> DataResource.loading(data?.let(transform))
    }
} catch (t: Throwable) {
    DataResource.error(t)
}

inline fun <T, R> DataResource<T>.flatMap(
    transform: (data: T) -> DataResource<R>,
): DataResource<R> = try {
    when (this) {
        is DataResource.Success -> transform(data)
        is DataResource.Error -> DataResource.error(throwable)
        is DataResource.Loading -> data?.let { transform(it) } ?: DataResource.loading(null)
    }
} catch (t: Throwable) {
    DataResource.error(t)
}

fun <T> Flow<DataResource<T>>.onSuccess(action: suspend (T) -> Unit): Flow<DataResource<T>> =
    onEach { resource ->
        if (resource is DataResource.Success<T>) {
            action(resource.data)
        }
    }

fun <T> Flow<DataResource<T>>.onError(action: suspend (Throwable) -> Unit): Flow<DataResource<T>> =
    onEach { resource ->
        if (resource is DataResource.Error) {
            action(resource.throwable)
        }
    }

suspend fun <T> Flow<DataResource<T>>.awaitOrThrow(): T =
    transform { resource ->
        when (resource) {
            is DataResource.Success -> emit(resource.data)
            is DataResource.Error -> throw resource.throwable
            is DataResource.Loading -> return@transform
        }
    }.first()


fun <T, R> Flow<DataResource<T>>.flatMapDataResource(operation: (T) -> Flow<DataResource<R>>): Flow<DataResource<R>> =
    transform { resource ->
        when (resource) {
            is DataResource.Success -> emitAll(operation(resource.data))
            is DataResource.Error -> emit(DataResource.error(resource.throwable))
            is DataResource.Loading -> {
                val data = resource.data?.let(operation) ?: return@transform
                emitAll(data)
            }
        }
    }

suspend fun <T> Flow<DataResource<T>>.collectDataResource(
    onSuccess: suspend (T) -> Unit,
    onError: (Throwable) -> Unit,
    onLoading: (T?) -> Unit = {},
) {
    this.catch { onError(it) }
        .collect {
            when (it) {
                is DataResource.Error -> onError(it.throwable)
                is DataResource.Loading -> onLoading.invoke(it.data)
                is DataResource.Success -> onSuccess(it.data)
            }
        }
}

fun <Source, Destination> Flow<DataResource<Source>>.mapDataResource(mapper: (Source) -> Destination)
    : Flow<DataResource<Destination>> =
    map {
        when (it) {
            is DataResource.Success -> DataResource.success(mapper(it.data))
            is DataResource.Error -> DataResource.error(it.throwable)
            is DataResource.Loading -> {
                val newData = it.data?.let { source -> mapper(source) }
                DataResource.loading(newData)
            }
        }
    }

fun <Source, Destination> Flow<DataResource<List<Source>>>.mapListDataResource(mapper: (Source) -> Destination)
    : Flow<DataResource<List<Destination>>> =
    mapDataResource { list -> list.map(mapper) }
