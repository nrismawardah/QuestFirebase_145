package com.example.pam15.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam15.model.Mahasiswa
import com.example.pam15.repository.RepositoryMhs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpdateViewModel(
    private val repository: RepositoryMhs
) : ViewModel() {

    private val _mahasiswaState = MutableStateFlow<Mahasiswa?>(null)
    val mahasiswaState: StateFlow<Mahasiswa?> = _mahasiswaState

    private val _updateUiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val updateUiState: StateFlow<UpdateUiState> = _updateUiState

    fun getMhs(nim: String) {
        viewModelScope.launch {
            repository.getMhs(nim)
                .catch { e ->
                    _mahasiswaState.value = null
                }
                .collectLatest { mahasiswa ->
                    _mahasiswaState.value = mahasiswa
                }
        }
    }

    fun updateMahasiswa(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            _updateUiState.value = UpdateUiState.Loading
            try {
                repository.updateMhs(mahasiswa)
                _updateUiState.value = UpdateUiState.Success
            } catch (e: Exception) {
                _updateUiState.value = UpdateUiState.Error(e.message ?: "Gagal mengupdate data")
            }
        }
    }
}

sealed class UpdateUiState {
    object Idle : UpdateUiState()
    object Loading : UpdateUiState()
    object Success : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
}