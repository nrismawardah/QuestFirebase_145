package com.example.pam15.repository

import com.example.pam15.model.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NetworkRepositoryMhs(
    private val firestore: FirebaseFirestore
) : RepositoryMhs {

    override suspend fun insertMhs(mahasiswa: Mahasiswa) {
        try {
            firestore.collection("Mahasiswa").add(mahasiswa).await()
        } catch (e: Exception) {
            throw Exception("Gagal menambahkan data mahasiswa:${e.message}")
        }
    }

    override fun getAllMhs(): Flow<List<Mahasiswa>> = callbackFlow {
        // Membuka collection dari firestore
        val mhsCollection = firestore.collection("Mahasiswa")
            .orderBy("nim", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val mhsList = value.documents.mapNotNull {
                        // Convert dari document firestore ke dataclass
                        it.toObject(Mahasiswa::class.java)!!
                    }
                    // Fungsi untuk mengirim collection ke dataclass
                    trySend(mhsList)
                }
            }
        awaitClose {
            // Menutup collection dari firestore
            mhsCollection.remove()
        }
    }

    override suspend fun getMhs(nim: String): Flow<Mahasiswa> = callbackFlow {
        val mhsCollection = firestore.collection("Mahasiswa")
            .whereEqualTo("nim", nim)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                } else {
                    if (value != null && value.documents.isNotEmpty()) {
                        val mahasiswa = value.documents.firstOrNull()?.toObject(Mahasiswa::class.java)
                        mahasiswa?.let {
                            trySend(it)
                        } ?: close(Exception("Mahasiswa tidak ditemukan"))
                    } else {
                        close(Exception("Mahasiswa tidak ditemukan"))
                    }
                }
            }

        awaitClose { mhsCollection.remove() }
    }

    override suspend fun deleteMhs(mahasiswa: Mahasiswa) {
        try {
            val querySnapshot = firestore.collection("Mahasiswa")
                .whereEqualTo("nim", mahasiswa.nim)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents[0].id
                firestore.collection("Mahasiswa")
                    .document(documentId)
                    .delete()
                    .await()
            } else {
                throw Exception("Mahasiswa dengan NIM ${mahasiswa.nim} tidak ditemukan.")
            }
        } catch (e: Exception) {
            throw Exception("Error deleting Mahasiswa: ${e.message}")
        }
    }

    override suspend fun updateMhs(mahasiswa: Mahasiswa) {
        try {
            val querySnapshot = firestore.collection("Mahasiswa")
                .whereEqualTo("nim", mahasiswa.nim)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents[0].id
                firestore.collection("Mahasiswa")
                    .document(documentId)
                    .set(mahasiswa)
                    .await()
            } else {
                throw Exception("Mahasiswa dengan NIM ${mahasiswa.nim} tidak ditemukan.")
            }
        } catch (e: Exception) {
            throw Exception("Gagal mengupdate data mahasiswa: ${e.message}")
        }
    }
}