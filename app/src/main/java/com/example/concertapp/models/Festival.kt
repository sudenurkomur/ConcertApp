package com.example.concertapp.models

data class Festival(
    val id: Long, // Festival ID'si
    val name: String, // Festival Adı
    val desc: String, // Festival Açıklaması
    val start_date: String, // Festival Başlangıç Tarihi
    val end_date: String, // Festival Bitiş Tarihi
    val events: List<Event>? = null // Festivale bağlı etkinlikler
)
