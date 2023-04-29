package com.example.cameraapp.login.database

import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun getUser(user : User) : Flow<User>

    suspend fun registerUser(user : User) : Flow<Int>

    suspend fun updatePassword(username : String, oldPassword : String, newPassword : String) : Flow<Int>
}