import okhttp3.MultipartBody
import retrofit2.http.*

interface SupabaseService {

    // Kullanıcı bilgilerini sorgulama
    @GET("/rest/v1/Users")
    suspend fun getUser(
        @Query("username") username: String,
        @Query("password") password: String,
        @Header("apikey") apiKey: String
    ): List<User>

    // Kullanıcı kaydı
    @POST("/rest/v1/Users")
    suspend fun signUpUser(
        @Body newUser: SignUpUser,
        @Header("apikey") apiKey: String
    )

    // Festivalleri çekme
    @GET("/rest/v1/Festivals")
    suspend fun getFestivals(
        @Header("apikey") apiKey: String
    ): List<Festival>

    // Festival ekleme
    @POST("/rest/v1/Festivals")
    suspend fun addFestival(
        @Header("apikey") apiKey: String,
        @Body festival: Festival
    )

    // Festival silme
    @DELETE("/rest/v1/Festivals")
    suspend fun deleteFestival(
        @Query("id") idFilter: String, // Örneğin: eq.1
        @Header("apikey") apiKey: String
    )

    // Festival güncelleme
    @PATCH("/rest/v1/Festivals")
    suspend fun updateFestival(
        @Query("id") idFilter: String, // Örneğin: eq.1
        @Header("apikey") apiKey: String,
        @Body festival: Festival
    )

    // Event ekleme
    @POST("/rest/v1/Stages")
    suspend fun addEvent(
        @Header("apikey") apiKey: String,
        @Body event: Event
    )

    // Eventleri çekme
    @GET("/rest/v1/Stages")
    suspend fun getEvents(
        @Query("festival_id") filter: String,
        @Header("apikey") apiKey: String
    ): List<Event>

    // Eventleri silme
    @DELETE("/rest/v1/Stages")
    suspend fun deleteEvent(
        @Query("id") id: String, // "id" olarak değiştirildi ve tip String yapıldı
        @Header("apikey") apiKey: String
    )

    // Event güncelleme
    @PATCH("/rest/v1/Stages")
    suspend fun updateEvent(
        @Query("id") stageId: String, // Supabase'de "eq.<id>" formatını kullanır.
        @Header("apikey") apiKey: String,
        @Body stage: Event // Gövdeye güncellenmiş Event verilerini gönderir.
    )


    // Resim yükleme
    @POST("/storage/v1/object/{bucket}/{path}")
    @Multipart
    suspend fun uploadImage(
        @Path("bucket") bucket: String,
        @Path("path", encoded = true) path: String,
        @Part file: MultipartBody.Part,
        @Header("Authorization") apiKey: String
    )

    // Public URL alma
    @GET("/storage/v1/object/public/{bucket}/{path}")
    suspend fun getPublicUrl(
        @Path("bucket") bucket: String,
        @Path("path", encoded = true) path: String,
        @Header("Authorization") apiKey: String
    ): String

    // Kullanıcının kaydettiği etkinlikleri getir
    @GET("/rest/v1/UserSavedEvents")
    suspend fun getUserSavedEvents(
        @Query("user_id") userId: String, // eq.<user_id>
        @Header("apikey") apiKey: String
    ): List<SaveEventRequest>

    // Etkinlik kaldır
    @DELETE("/rest/v1/UserSavedEvents")
    suspend fun deleteSavedEvent(
        @Query("user_id") userId: String, // eq.<user_id>
        @Query("event_id") eventId: String, // eq.<event_id>
        @Header("apikey") apiKey: String
    )

    // Etkinlik kaydet
    @POST("/rest/v1/UserSavedEvents")
    suspend fun saveEvent(
        @Header("apikey") apiKey: String,
        @Body saveEventRequest: SaveEventRequest
    )




    @GET("/rest/v1/Stages")
    suspend fun getEventById(
        @Query("id") eventIdFilter: String, // eq.<event_id>
        @Header("apikey") apiKey: String
    ): List<Event>


}