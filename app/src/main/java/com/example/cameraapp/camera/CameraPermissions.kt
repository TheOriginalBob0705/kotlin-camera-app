package com.example.cameraapp

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
    permission : String = Manifest.permission.CAMERA,
    reason : String = "Camera permissions are needed for the app to work",
    permissionDeniedOutput : @Composable () -> Unit = {},
    output : @Composable () -> Unit = {}
) {
    val permissionState = rememberPermissionState(permission)
    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = {
            Reason(
                message = reason,
                onRequestPermission = { permissionState.launchPermissionRequest() }
            )
        },
        permissionNotAvailableContent = permissionDeniedOutput,
        content = output
    )
}

@Composable
private fun Reason(
    message : String,
    onRequestPermission : () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /*NONE*/ },
        title = {
            Text(text = "Permission request")
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Ok")
            }
        }
    )
}