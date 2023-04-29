package com.example.cameraapp.login.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    fun getUser(username: String, password: String) : Flow<User>

    @Query("UPDATE users SET password = :newPassword WHERE username = :username AND password = :oldPassword")
    fun updatePassword(username: String, oldPassword: String, newPassword: String) : Flow<Int>

    @Query("INSERT INTO users (username, password) VALUES (:username, :password)")
    fun registerUser(username: String, password: String) : Flow<Int>
}