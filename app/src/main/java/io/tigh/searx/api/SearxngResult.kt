package io.tigh.searx.api

import com.google.gson.annotations.SerializedName

data class SearxngResultItem (
    val title: String,
    val content: String,
    val url: String,
    @SerializedName("img_src") val imgSrc: String,
    val engine: String,

    @SerializedName("parsed_url") val parsedURL: List<String>,
    val template: String,
    val engines: List<String>,
    val score: Number,
    val category: String

)

data class SearxngResult (
    val query: String,
    @SerializedName("number_of_results") val numberOfResults: Int,
    val results: List<SearxngResultItem>
)