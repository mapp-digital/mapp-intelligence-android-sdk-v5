package webtrekk.android.sdk.util

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T : Any> Delegates.notNullOrException(errorMessage: String): ReadWriteProperty<Any?, T> =
    NotNullOrException(errorMessage)

class NotNullOrException<T : Any>(errorMessage: String) : ReadWriteProperty<Any?, T> {
    private var value: T? = null
    private val errorMsg = errorMessage

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException(errorMsg)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}
