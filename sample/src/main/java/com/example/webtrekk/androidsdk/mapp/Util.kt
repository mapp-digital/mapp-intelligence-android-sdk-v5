package com.example.webtrekk.androidsdk.mapp

internal object Util {
    @JvmStatic
    fun capitalize(inputWord: String): String {
        val words = inputWord.toLowerCase().split("_".toRegex()).toTypedArray()
        val builder = StringBuilder()
        for (i in words.indices) {
            val word = words[i]
            if (i > 0 && word.isNotEmpty()) {
                builder.append("_")
            }
            val cap = word.substring(0, 1).toUpperCase() + word.substring(1)
            builder.append(cap)
        }
        return builder.toString()
    }
}