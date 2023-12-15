package io.tigh.searx.api

import android.content.Context
import android.util.Log

class Searxng(val host: String) {
    lateinit var q: String
    lateinit var format: String

    fun search(ctx: Context) {
        Log.d(ctx.javaClass.name, "searching for " + q + " in format " + format +
            " against host " + host
        )
    }
}