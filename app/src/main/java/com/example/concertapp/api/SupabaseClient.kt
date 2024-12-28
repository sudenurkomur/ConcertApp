package com.example.concertapp.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private const val BASE_URL = "https://mfamdgofdkgnjqjehxtn.supabase.co"
    private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1mYW1kZ29mZGtnbmpxamVoeHRuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzUyOTA1MzgsImV4cCI6MjA1MDg2NjUzOH0.xnD6g4uOksQstn-PNE94v6D8MurXWc7_83S_cEeaP7U"

    // Interceptor ile gerekli başlıkları ekleme
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $API_KEY")
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        })
        .build()

    // Retrofit oluşturma
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Supabase hizmeti
    val supabaseService: SupabaseService = retrofit.create(SupabaseService::class.java)

    // Supabase Storage URL
    fun getPublicStorageUrl(bucketName: String, fileName: String): String {
        return "$BASE_URL/storage/v1/object/public/$bucketName/$fileName"
    }
}