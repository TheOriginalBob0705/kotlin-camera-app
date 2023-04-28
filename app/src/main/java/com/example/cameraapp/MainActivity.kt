package com.example.cameraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cameraapp.ui.theme.CameraAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    CameraPreviewContent(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun CameraPreviewContent(modifier : Modifier = Modifier) {
    Permission(
        permission = android.Manifest.permission.CAMERA,
        reason = "This app requires permission to use the camera in order to work properly",
        permissionDeniedOutput = {
            Column(modifier) {
                Text("Permission denied")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { /* TODO */ }) {
                }
            }
        }
    ) {
        Text("Permission granted")
    }
}

@ExperimentalPermissionsApi
@Composable
fun Permission(
    permission : String = android.Manifest.permission.CAMERA,
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
