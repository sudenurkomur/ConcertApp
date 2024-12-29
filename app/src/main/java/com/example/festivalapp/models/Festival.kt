data class Festival(
    val id: Long? = null,
    val name: String,
    val start_date: String, // Supabase sütun adı ile eşleşmeli
    val end_date: String,   // Supabase sütun adı ile eşleşmeli
    val description: String? = null
)
