package webtrekk.android.sdk.extension

import java.net.URLEncoder

internal inline fun <reified T : Any> T?.nullOrEmptyThrowError(propertyName: T): T {
    when (this) {
        is String? -> if (this.isNullOrBlank())
            error("$propertyName is missing in the configurations. $propertyName is required in the configurations.")

        is List<*>? -> if (this.isNullOrEmpty())
            error("$propertyName is missing in the configurations. $propertyName is required in the configurations.")
    }

    return this as T
}

internal fun <T : Any> List<T>?.validateEntireList(propertyName: Any): List<T> {
    this.nullOrEmptyThrowError(propertyName)

    this?.forEach {
        it.nullOrEmptyThrowError(propertyName)
    }

    return this as List<T>
}

internal fun String.encodeToUTF8(): String = URLEncoder.encode(this, "UTF-8")
