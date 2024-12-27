package com.example.concertapp.api

import com.example.concertapp.models.DataClass
import com.example.concertapp.models.User
import retrofit2.Call
import retrofit2.http.*

interface SupabaseService {

    @GET("rest/v1/Events")
    fun getFestivals(
        @Query("select") fields: String = "*"
    ): Call<List<DataClass>>

    @POST("rest/v1/Events")
    fun addEvent(
        @Body event: DataClass
    ): Call<Void>

    @PATCH("rest/v1/Events")
    fun updateFestival(
        @Query("id") id: String,
        @Body updatedFestival: Map<String, Any>
    ): Call<Void>

    @DELETE("rest/v1/Events")
    fun deleteFestival(
        @Query("id") id: String
    ): Call<Void>

    @POST("rest/v1/LoginInfo")
    fun addUser(
        @Body user: User
    ): Call<Void>

    @GET("rest/v1/LoginInfo")
    fun getUser(
        @Query("email") email: String, // Kullanıcının e-posta adresi
        @Query("password") password: String // Kullanıcının şifresi
    ): Call<List<User>>
}
