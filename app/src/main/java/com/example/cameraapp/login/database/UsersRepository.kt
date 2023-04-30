package com.example.cameraapp.login.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsersRepository(private val userDao : UserDao) {
    suspend fun getUserByUsername(username : String) : User {
        return withContext(Dispatchers.IO) {
            val user = userDao.getUserByUsername(username)
            user
        }
    }

    suspend fun registerUser(user : User) {
        withContext(Dispatchers.IO) {
            userDao.registerUser(user)
        }
    }
}