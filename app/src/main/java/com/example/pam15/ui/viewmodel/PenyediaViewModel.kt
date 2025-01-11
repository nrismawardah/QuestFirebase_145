package com.example.pam15.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pam15.MahasiswaApp

object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer { HomeViewModel(mahasiswaApp().containerApp.repositoryMhs) }
    }
}

fun CreationExtras.mahasiswaApp(): MahasiswaApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MahasiswaApp)