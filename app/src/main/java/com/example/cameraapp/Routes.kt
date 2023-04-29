package com.example.cameraapp

sealed class Routes(val route : String) {
    object Login : Routes("Login")

    object SignUp : Routes("SignUp")

    object ForgotPassword : Routes("ForgotPassword")

    object CameraPreview : Routes("CameraPreview")
}