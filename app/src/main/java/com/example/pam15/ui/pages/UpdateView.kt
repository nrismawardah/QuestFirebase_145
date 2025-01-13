package com.example.pam15.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pam15.model.Mahasiswa
import com.example.pam15.ui.customwidget.TopAppBar
import com.example.pam15.ui.viewmodel.FormErrorState
import com.example.pam15.ui.viewmodel.MahasiswaEvent
import com.example.pam15.ui.viewmodel.PenyediaViewModel
import com.example.pam15.ui.viewmodel.UpdateUiState
import com.example.pam15.ui.viewmodel.UpdateViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UpdateView(
    mahasiswa: Mahasiswa,
    viewModel: UpdateViewModel = viewModel(factory = PenyediaViewModel.Factory),
    onUpdateSuccess: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val updateUiState by viewModel.updateUiState.collectAsState()
    val mahasiswaEvent = remember {
        mutableStateOf(
            MahasiswaEvent(
                nama = mahasiswa.nama,
                nim = mahasiswa.nim,
                gender = mahasiswa.gender,
                alamat = mahasiswa.alamat,
                kelas = mahasiswa.kelas,
                angkatan = mahasiswa.angkatan
            )
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(updateUiState) {
        when (updateUiState) {
            is UpdateUiState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Data berhasil diperbarui")
                }
                delay(700)
                onUpdateSuccess()
            }
            is UpdateUiState.Error -> {
                val errorMessage = (updateUiState as UpdateUiState.Error).message
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp).offset(y = (-40).dp)
        ) {
            TopAppBar(
                onBack = onCancel,
                showBackButton = true,
                judul = "Edit Mahasiswa"
            )
            FormMahasiswa(
                mahasiswaEvent = mahasiswaEvent.value,
                onValueChange = { updatedEvent ->
                    mahasiswaEvent.value = updatedEvent
                },
                errorState = FormErrorState(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateMahasiswa(
                        mahasiswa.copy(
                            nama = mahasiswaEvent.value.nama,
                            alamat = mahasiswaEvent.value.alamat,
                            gender = mahasiswaEvent.value.gender,
                            kelas = mahasiswaEvent.value.kelas,
                            angkatan = mahasiswaEvent.value.angkatan
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (updateUiState is UpdateUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Updating...")
                } else {
                    Text("Update")
                }
            }
        }
    }
}