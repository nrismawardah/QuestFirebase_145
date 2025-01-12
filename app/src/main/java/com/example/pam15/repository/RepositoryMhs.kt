package com.example.pam15.repository

import com.example.pam15.model.Mahasiswa
import kotlinx.coroutines.flow.Flow

interface RepositoryMhs {

    suspend fun insertMhs(mahasiswa: Mahasiswa)

    fun getAllMhs(): Flow<List<Mahasiswa>>

    suspend fun getMhs(nim: String): Flow<Mahasiswa>

    suspend fun deleteMhs(mahasiswa: Mahasiswa)

    suspend fun updateMhs(mahasiswa: Mahasiswa)

}