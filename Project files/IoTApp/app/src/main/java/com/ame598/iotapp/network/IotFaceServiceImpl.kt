package com.ame598.iotapp.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "http://192.168.0.57:1234"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()


/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    //.addConverterFactory(MoshiConverterFactory.create(moshi))
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(client)
    .build()

/**
 * A public interface that exposes the [getPhotos] method
 */
interface IotFaceServiceImpl {
    /**
     * Returns a [List] of [MarsPhoto] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("/api/getNewFace")
    suspend fun getPhotos(): IotFaceObject

    @GET("/api/approveface")
    suspend fun approve(): SimpleResponse

    @GET("/api/approveonceface")
    suspend fun approveonce(): SimpleResponse

    @GET("/api/denyface")
    suspend fun deny(): SimpleResponse
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object IotApi {
    val retrofitService: IotFaceServiceImpl by lazy { retrofit.create(IotFaceServiceImpl::class.java) }
}