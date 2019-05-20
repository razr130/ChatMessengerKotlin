package com.example.chatmessenger.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users (var uid: String, var username: String, var email: String, var password: String, var image: String) : Parcelable{
    constructor() : this("","","","","")
}