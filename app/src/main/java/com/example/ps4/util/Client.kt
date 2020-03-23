package com.example.ps4.util

data class Client(
    var id: Int, var name: String = "", var surname: String = "", var birthDate: String = "",
    var email: String = "", var instagram: String = "", var number: String = "",
    var whatsapp: String = "",
    var photo: String = ""
)