package com.example.cameraapp.login.database

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUserByUsername(username : String) : User

    @Insert
    fun registerUser(user : User)
}