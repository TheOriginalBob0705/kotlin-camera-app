package com.example.cameraapp.camera

import android.Manifest
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
@Composable
fun Permission(
    permission: String = Manifest.permission.CAMERA,
    reason: String = "Camera permissions are needed for the app to work",
    permissionDeniedOutput: @Composable () -> Unit = {},
    output: @Composable () -> Unit = {}
) {
    val permissionState = rememberPermissionState(permission)
    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(text = "Permission request") },
                text = { Text(reason) },
                confirmButton = {
                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Ok")
                    }
                }
            )
        },
        permissionNotAvailableContent = permissionDeniedOutput,
        content = output
    )
}
