package com.example.concertapp.models

data class User(
    val id: String? = null, // ID otomatik atanabilir
    val email: String,
    val password: String,
    val role: String = "user" // Varsayılan rol kullanıcı
)
