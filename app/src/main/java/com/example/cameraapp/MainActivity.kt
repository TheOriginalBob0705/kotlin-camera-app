package com.example.cameraapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cameraapp.ui.theme.CameraAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.launch

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
        reason = "This app requires permission to use the camera",
        permissionDeniedOutput = {
            Column(modifier) {
                Text("Permission denied")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { /* TODO make a button to take you to settings and set the permission */ }) {
                }
            }
        }
    ) {
        CameraPreview(modifier)
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

@Composable
fun CameraPreview(
    modifier : Modifier = Modifier,
    camScale : PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    camSelector : CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {
    val corScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = camScale
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            corScope.launch {
                val cameraProvider = context.getCameraProvider()
                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, camSelector, previewUseCase
                    )
                } catch (ex : Exception) {
                    Log.e("fun: CameraPreview", "Couldn't bind use case", ex)
                }
            }
            previewView
        }
    )
}

suspend fun Context.getCameraProvider() : ProcessCameraProvider = suspendCoroutine { continuationProvider ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuationProvider.resume(future.get())
        }, executor)
    }
}

val Context.executor : Executor
    get() = ContextCompat.getMainExecutor(this)
