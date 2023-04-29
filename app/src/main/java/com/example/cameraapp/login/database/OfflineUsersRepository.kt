package com.example.cameraapp.login.database

import kotlinx.coroutines.flow.Flow

class OfflineUsersRepository(private val userDao : UserDao) : UsersRepository {
    override fun getUser(user: User): Flow<User> = userDao.getUser(user.username, user.password)

    override suspend fun registerUser(user: User): Flow<Int> = userDao.registerUser(user.username, user.password)

    override suspend fun updatePassword(
        username: String,
        oldPassword: String,
        newPassword: String
    ): Flow<Int> = userDao.updatePassword(username, oldPassword, newPassword)
}