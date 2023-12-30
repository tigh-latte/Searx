package io.tigh.searx.config

import android.content.Context

object Config {
    var baseURL: String = "http://192.168.250.13"
    var autocompleteProvider: String = "duckduckgo"

    fun save(ctx: Context) {
        val pref = ctx.getSharedPreferences("searx_config", Context.MODE_PRIVATE) ?: return
        with(pref.edit()) {
            putString("searx_default_base_url", baseURL)
            putString("searx_default_autocomplete_provider", autocompleteProvider)
            apply()
        }
    }
}