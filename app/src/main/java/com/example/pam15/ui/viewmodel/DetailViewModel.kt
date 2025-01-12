package com.example.pam15.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam15.model.Mahasiswa
import com.example.pam15.repository.RepositoryMhs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repomhs: RepositoryMhs
) : ViewModel() {

    private val nim: String = checkNotNull(savedStateHandle["nim"])

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState

    init {
        getMhsbyNim()
    }

    fun getMhsbyNim() {
        viewModelScope.launch {
            _detailUiState.value = DetailUiState.Loading

            repomhs.getMhs(nim)
                .onStart {
                }
                .catch {
                    _detailUiState.value = DetailUiState.Error
                }
                .collect { mahasiswa ->
                    _detailUiState.value = DetailUiState.Success(mahasiswa)
                }
        }
    }
}

sealed class DetailUiState {
    data class Success(val mahasiswa: Mahasiswa) : DetailUiState()
    object Error : DetailUiState()
    object Loading : DetailUiState()
}
