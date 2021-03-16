package webtrekk.android.sdk.events.eventParams

/**
 * Created by Aleksandar Marinkovic on 3/12/21.
 * Copyright (c) 2021 MAPP.
 */
interface BaseEvent {
    fun toHasMap(): MutableMap<String, String>
}