package webtrekk.android.sdk.extension

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T : Any> Delegates.initOrException(errorMessage: String): ReadWriteProperty<Any?, T> =
    InitOrException(errorMessage)

class InitOrException<T : Any>(errorMessage: String) : ReadWriteProperty<Any?, T> {
    private var value: T? = null
    private val errorMsg = errorMessage

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException(errorMsg)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}
