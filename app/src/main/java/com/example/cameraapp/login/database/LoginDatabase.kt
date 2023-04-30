package com.example.cameraapp.login.database

import android.content.Context
import androidx.room.*

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class LoginDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao

    companion object {
        @Volatile
        private var Instance : LoginDatabase? = null

        fun getDatabase(context : Context) : LoginDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, LoginDatabase::class.java, "login_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
