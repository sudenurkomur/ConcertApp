package com.example.concertapp.api

import UpdatedFestival
import com.example.concertapp.models.Event
import com.example.concertapp.models.Festival
import com.example.concertapp.models.User
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface SupabaseService {

    // Festival ve ilişkili etkinlikleri (events) almak için
    @GET("rest/v1/Festivals")
    fun getFestivalsWithEvents(
        @Query("select") fields: String = "*, Events(*)"
    ): Call<List<Festival>>

    @GET("rest/v1/Festivals")
    fun getFestivalById(
        @Query("id") id: String
    ): Call<List<Festival>>

    // Yeni bir festival eklemek için
    @POST("rest/v1/Festivals")
    fun addFestival(
        @Body festival: Festival
    ): Call<Void>

    // Bir festivali güncellemek için
    @PATCH("rest/v1/Festivals")
    fun updateFestival(
        @Query("id") id: String,
        @Body updatedFestival: UpdatedFestival
    ): Call<Void>

    // Bir festivali silmek için
    @DELETE("rest/v1/Festivals")
    fun deleteFestival(
        @Query("id") id: String
    ): Call<Void>



    // Yeni bir etkinlik (event) eklemek için
    @POST("rest/v1/Events")
    fun addEvent(
        @Body event: Event
    ): Call<Void>

    // Bir etkinliği güncellemek için
    @PATCH("rest/v1/Events")
    fun updateEvent(
        @Query("id") id: String,
        @Body updatedEvent: Map<String, String>
    ): Call<Void>

    // Bir etkinliği silmek için
    @DELETE("rest/v1/Events")
    fun deleteEvent(
        @Query("id") id: String
    ): Call<Void>

    // Bir etkinlikleri almak için
    @GET("rest/v1/Events")
    fun getEvents(
        @Query("select") fields: String = "*"
    ): Call<List<Event>>

    @GET("rest/v1/Events")
    fun getEventsForFestival(
        @Query("festival_id") festivalId: String // String türü kullanıyoruz
    ): Call<List<Event>>

    @GET("rest/v1/Events")
    fun getEventById(
        @Query("id") eventId: String
    ): Call<List<Event>>


    // Yeni kullanıcı kaydı yapmak için
    @POST("rest/v1/LoginInfo")
    fun addUser(
        @Body user: User
    ): Call<Void>

    // Kullanıcıyı doğrulamak için
    @GET("rest/v1/LoginInfo")
    fun getUser(
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<List<User>>

    @Multipart
    @POST("/storage/v1/object/{bucket}/{fileName}")
    fun uploadImage(
        @Path("bucket") bucket: String, // Depolama birimi adı (örn: "event-images")
        @Path("fileName") fileName: String, // Dosya adı
        @Part file: MultipartBody.Part // Yüklenecek dosya
    ): Call<Void> // Başarılıysa geri dönüş değeri yok

    @GET("/storage/v1/object/public/{bucket}/{fileName}")
    fun getPublicUrl(
        @Path("bucket") bucket: String, // Depolama birimi adı
        @Path("fileName") fileName: String // Dosya adı
    ): Call<String> // Geri dönen public URL
}
