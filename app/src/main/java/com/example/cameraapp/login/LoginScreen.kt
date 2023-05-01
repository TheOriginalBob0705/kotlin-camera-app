package com.example.cameraapp.login

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.example.cameraapp.camera.MainCameraPreview
import com.example.cameraapp.Routes
import com.example.cameraapp.login.database.UsersRepository
import com.example.cameraapp.login.models.LoginViewModel
import com.example.cameraapp.login.models.SignUpViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPermissionsApi
@ExperimentalCoilApi
@ExperimentalCoroutinesApi
@Composable
fun LoginScreenMain(usersRep: UsersRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Login.route) {
        composable(Routes.Login.route) { LoginPage(navController, LoginViewModel(usersRep)) }
        composable(Routes.SignUp.route) { SignUp(navController, SignUpViewModel(usersRep)) }
        composable(Routes.CameraPreview.route) { MainCameraPreview() }
    }
}