package com.example.concertapp

data class DataClass(
    var dataTitle: String? = null,   // Festival başlığı
    var dataStage: String? = null,  // Sahne adı
    var dataSinger: String? = null, // Şarkıcı adı
    var dataTime: String? = null,   // Zaman
    var key: String? = null         // Firebase'deki benzersiz key
)
