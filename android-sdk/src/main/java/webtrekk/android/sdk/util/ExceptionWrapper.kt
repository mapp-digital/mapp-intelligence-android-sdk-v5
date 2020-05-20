package webtrekk.android.sdk.util

import kotlin.Exception

internal class ExceptionWrapper constructor(val name: String, val customMessage: String) : Exception()