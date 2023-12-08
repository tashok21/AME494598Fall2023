package com.ame598.clinetappbasic

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/checkthisface")
    fun sendString(@Body body: User): Call<ResponseBody>
}


