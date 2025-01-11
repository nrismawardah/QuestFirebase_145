package com.example.pam15.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam15.model.Mahasiswa
import com.example.pam15.repository.RepositoryMhs
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HomeViewModel (
    private val repomhs: RepositoryMhs
) : ViewModel() {

    var mhsUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getMhs()
    }

    fun getMhs (){
        viewModelScope.launch {
            repomhs.getAllMhs().onStart {
                mhsUiState = HomeUiState.Loading
            }
                .catch {
                    mhsUiState = HomeUiState.Error(e = it)
                }
                .collect {
                    mhsUiState = if (it.isEmpty()){
                        HomeUiState.Error(Exception("Belum ada data"))
                    }
                    else{
                        HomeUiState.Success(it)
                    }
                }
        }
    }

    fun deleteMhs(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            try { repomhs.deleteMhs(mahasiswa)
            } catch (e: Exception) {
                mhsUiState = HomeUiState.Error(e)
            }
        }
    }
}


sealed class HomeUiState {

    // Sukses
    data class Success(val data: List<Mahasiswa>) : HomeUiState()

    // Error
    data class Error(val e: Throwable) : HomeUiState()

    // Loading
    object Loading : HomeUiState()

}