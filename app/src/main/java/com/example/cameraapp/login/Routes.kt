package com.example.cameraapp.login

sealed class Routes(val route : String) {
    object Login : Routes("Login")
}