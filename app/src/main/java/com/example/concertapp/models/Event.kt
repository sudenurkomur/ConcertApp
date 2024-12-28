package com.example.concertapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val id: Long? = 0, // Event ID'si
    val title: String, // Etkinlik Başlığı
    val stage: String, // Etkinlik Sahnesi
    val singer: String, // Etkinlik Şarkıcısı
    val date: String, // Etkinlik Tarihi
    val imageUrl: String? = null, // Image Linki
    val location: Location? = null, // Etkinlik Konumu
    val festival_id: Long // Bağlı olduğu festivalin ID'si
) : Parcelable

@Parcelize
data class Location(
    val latitude: Double, // Enlem
    val longitude: Double // Boylam
) : Parcelable
