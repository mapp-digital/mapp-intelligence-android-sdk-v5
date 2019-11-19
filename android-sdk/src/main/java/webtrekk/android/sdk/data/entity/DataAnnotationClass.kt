package webtrekk.android.sdk.data.entity

import webtrekk.android.sdk.TrackParams

/**
 * Created by Aleksandar Marinkovic on 2019-11-19.
 * Copyright (c) 2019 MAPP.
 */
internal data class DataAnnotationClass(
    val trackRequest: TrackRequest,
    val trackParams: Array<TrackParams>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataAnnotationClass

        if (trackRequest != other.trackRequest) return false
        if (!trackParams.contentEquals(other.trackParams)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trackRequest.hashCode()
        result = 31 * result + trackParams.contentHashCode()
        return result
    }
}