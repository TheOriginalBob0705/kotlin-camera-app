package com.example.cameraapp

sealed class Routes(val route : String) {
    object Login : Routes("Login")

    object SignUp : Routes("SignUp")

    object CameraPreview : Routes("CameraPreview")
}