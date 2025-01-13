package com.example.pam15.ui.navigasi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pam15.ui.pages.DetailView
import com.example.pam15.ui.pages.HomeView
import com.example.pam15.ui.pages.InsertMhsView
import com.example.pam15.ui.pages.UpdateView
import com.example.pam15.ui.viewmodel.PenyediaViewModel
import com.example.pam15.ui.viewmodel.UpdateViewModel

@Composable
fun PengelolaHalaman(
    modifier: Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = Modifier
    ) {
        composable(
            DestinasiHome.route
        ) {
            HomeView(
                navigateToItemEntry = {
                    navController.navigate(DestinasiInsert.route)
                },
                onDetailClick = { nim ->
                    navController.navigate("${DestinasiDetail.route}/$nim")
                    println("PengelolaHalaman: nim = $nim")
                }
            )
        }
        composable(
            DestinasiInsert.route
        ) {
            InsertMhsView(
                onBack = {
                    navController.popBackStack()
                },
                onNavigate = {
                    navController.navigate(DestinasiHome.route)
                }
            )
        }
        composable(
            DestinasiDetail.routesWithArg,
            arguments = listOf(
                navArgument(DestinasiDetail.NIM) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val nim =
                backStackEntry.arguments?.getString(DestinasiDetail.NIM)

            nim?.let {
                DetailView(
                    nim = nim,
                    onBack = { navController.popBackStack() },
                    onUpdate = { nim ->
                        navController.navigate(DestinasiUpdate.routeWithArg.replace("{${DestinasiUpdate.NIM}}", nim))
                    },
                )
            }
        }
        composable(
            DestinasiUpdate.routeWithArg
        ) { backStackEntry ->
            val nim = backStackEntry.arguments?.getString(DestinasiUpdate.NIM) ?: ""
            val viewModel: UpdateViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val mahasiswaState by viewModel.mahasiswaState.collectAsState(initial = null)
            SideEffect {
                viewModel.getMhs(nim)
            }
            mahasiswaState?.let { mahasiswa ->
                UpdateView(
                    mahasiswa = mahasiswa,
                    onUpdateSuccess = {
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
