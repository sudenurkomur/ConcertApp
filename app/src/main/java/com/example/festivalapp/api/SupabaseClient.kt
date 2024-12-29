import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private const val BASE_URL = "https://mfamdgofdkgnjqjehxtn.supabase.co"
    public const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1mYW1kZ29mZGtnbmpxamVoeHRuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzUyOTA1MzgsImV4cCI6MjA1MDg2NjUzOH0.xnD6g4uOksQstn-PNE94v6D8MurXWc7_83S_cEeaP7U"

    val service: SupabaseService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseService::class.java)
    }
}