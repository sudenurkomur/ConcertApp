package com.example.concertapp.models

data class DataClass(
    val id: String? = null, // Supabase'den dönen benzersiz kimlik (nullable)
    val title: String, // Festival adı
    val stage: String, // Sahne adı
    val singer: String, // Şarkıcı adı
    val date: String, // Festival tarihi
    val image: String? = null, // Görsel URL'si (nullable çünkü opsiyonel)
    val location: Location // Konum bilgisi
)

data class Location(
    val latitude: Double, // Enlem
    val longitude: Double // Boylam
)
