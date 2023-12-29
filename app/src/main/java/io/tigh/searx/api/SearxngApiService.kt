package io.tigh.searx.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearxngApiService {
    @GET("/search")
    suspend fun search(
        @Query("q") q: String,
        @Query("format") format: String
    ): Response<SearxngResult>
}