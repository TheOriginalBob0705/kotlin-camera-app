package com.example.cameraapp.login.database

import android.content.Context

interface AppContainer {
    val usersRepository : UsersRepository
}

class AppDataContainer(private val context : Context) : AppContainer {
    override val usersRepository: UsersRepository by lazy {
        OfflineUsersRepository(LoginDatabase.getDatabase(context).userDao())
    }
}