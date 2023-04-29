package com.example.cameraapp

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalCoilApi
@ExperimentalPermissionsApi
@Composable
fun Gallery(
    modifier : Modifier = Modifier,
    imgUri : (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri : Uri? ->
            imgUri(uri ?: EMPTY_IMG_URI)
        }
    )

    @Composable
    fun LaunchGallery() {
        SideEffect {
            launcher.launch("image/*")
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Permission(
            permission = Manifest.permission.ACCESS_MEDIA_LOCATION,
            reason = "Storage permissions are needed to access photos",
            permissionDeniedOutput = {
                Column(modifier) {
                    Text("Storage permissions denied")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Button(
                            modifier = Modifier.padding(10.dp),
                            onClick = {
                                context.startActivity(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                )
                            }
                        ) {
                            Text("Open settings")
                        }
                        Button(
                            modifier = Modifier.padding(10.dp),
                            onClick = {
                                imgUri(EMPTY_IMG_URI)
                            }
                        ) {
                            Text("Go back")
                        }
                    }
                }
            }
        ) {
            LaunchGallery()
        }
    } else {
        LaunchGallery()
    }
}