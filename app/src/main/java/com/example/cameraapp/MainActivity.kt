package com.example.cameraapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.room.Room
import coil.annotation.ExperimentalCoilApi
import com.example.cameraapp.login.LoginScreenMain
import com.example.cameraapp.login.database.LoginDatabase
import com.example.cameraapp.login.database.UsersRepository
import com.example.cameraapp.ui.theme.CameraAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

val EMPTY_IMG_URI : Uri = Uri.parse("file://dev/null")

@ExperimentalCoilApi
@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    private val usersRep by lazy {
        UsersRepository(
            Room.databaseBuilder(
                applicationContext,
                LoginDatabase::class.java,
                "login_db"
            ).build().userDao()
        )
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    LoginScreenMain(usersRep)
                }
            }
        }
    }
}
