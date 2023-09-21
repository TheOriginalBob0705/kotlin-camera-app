package com.example.cameraapp.network

import androidx.lifecycle.ViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface ImageUploadApi {
    @Multipart
    @POST("/upload")
    fun uploadImage(@Part image: MultipartBody.Part): Call<String>
}

class ImageUploadViewModel : ViewModel() {
    private val baseURL = "https://example.com/"

    private val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val api : ImageUploadApi = retrofit.create(ImageUploadApi::class.java)

    fun uploadImage(imageFile : File) {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        api.uploadImage(body).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                println("Response: ${response.body()}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }
}
