package com.example.concertapp.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private const val BASE_URL = "https://mfamdgofdkgnjqjehxtn.supabase.co"
    private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1mYW1kZ29mZGtnbmpxamVoeHRuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzUyOTA1MzgsImV4cCI6MjA1MDg2NjUzOH0.xnD6g4uOksQstn-PNE94v6D8MurXWc7_83S_cEeaP7U"

    // Interceptor ile hem Authorization hem de apikey başlıklarını ekliyoruz
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $API_KEY") // Authorization başlığı
                .addHeader("apikey", API_KEY) // apikey başlığı
                .addHeader("Content-Type", "application/json") // Content-Type başlığı
                .build()
            chain.proceed(request)
        })
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client) // Interceptor içeren client
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val supabaseService: SupabaseService = retrofit.create(SupabaseService::class.java)
}
