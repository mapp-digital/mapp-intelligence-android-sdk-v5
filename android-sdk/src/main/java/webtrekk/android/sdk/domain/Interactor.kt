package webtrekk.android.sdk.domain

import kotlinx.coroutines.CoroutineScope

internal interface InternalInteractor<in T, R : Any> {

    suspend operator fun invoke(invokeParams: T): Result<R>
}

internal interface ExternalInteractor<in T> {

    val scope: CoroutineScope

    operator fun invoke(invokeParams: T)
}
