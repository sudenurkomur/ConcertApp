data class Event(
    val id: Long? = null,
    val festival_id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val time: String, // Timestamp olarak tutulan tarih ve saat
    val image_url: String // Resim URL'si
) {
    val location: String
        get() = "$latitude, $longitude"
}
