package com.example.pam15

import android.app.Application
import com.example.pam15.di.MahasiswaContainer

class MahasiswaApp : Application() {
    lateinit var containerApp: MahasiswaContainer // Fungsinya untuk menyimpan instance dari MahasiswaContainer
    override fun onCreate() {
        super.onCreate()
        containerApp = MahasiswaContainer(this) // Membuat instance MahasiswaContainer
        // Instance = object yang dibuat dari class
    }
}