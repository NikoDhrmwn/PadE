package com.nrtxx.pade.api

import com.nrtxx.pade.helper.PenyakitResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("penyakit/{namaPenyakit}")
    fun getPenyakit(
        @Path("namaPenyakit") namaPenyakit: String
    ): Call<PenyakitResponse>
}