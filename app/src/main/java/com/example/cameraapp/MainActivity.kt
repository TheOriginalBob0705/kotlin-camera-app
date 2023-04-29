package com.example.cameraapp

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.cameraapp.ui.theme.CameraAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@ExperimentalCoilApi
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

@ExperimentalCoilApi
@ExperimentalPermissionsApi
@Composable
fun CameraPreviewContent(modifier : Modifier = Modifier) {
    val emptyImageUri = Uri.parse("file://dev/null")
    var imageUri by remember { mutableStateOf(emptyImageUri) }
    if (imageUri != emptyImageUri) {
        Box(modifier = modifier) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberImagePainter(imageUri),
                contentDescription = "Captured image"
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    imageUri = emptyImageUri
                }
            ) {
                Text("Go back")
            }
        }
    } else {
        CameraCapture(
            modifier = modifier,
            imgFile = { file ->
                imageUri = file.toUri()
            }
        )
    }
}

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

@ExperimentalPermissionsApi
@Composable
fun CameraCapture(
    modifier : Modifier = Modifier,
    camSelector : CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    imgFile : (File) -> Unit = {}
) {
    val context = LocalContext.current
    Permission(
        permission = Manifest.permission.CAMERA,
        reason = "Camera permissions are needed for the app to work",
        permissionDeniedOutput = {
            Column(modifier) {
                Text("Permissions denied")
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { /* TODO */
                }) {

                }
            }
        }
    ) {
        Box(modifier = modifier) {
            val lifecycleOwner = LocalLifecycleOwner.current
            val corScope = rememberCoroutineScope()
            var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
            val imageCaptureUseCase by remember {
                mutableStateOf(
                    ImageCapture.Builder()
                        .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()
                )
            }
            Box {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    useCase = { previewUseCase = it }
                )
                Button(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(20.dp)
                        .align(Alignment.BottomCenter),
                    onClick = {
                        corScope.launch {
                            imageCaptureUseCase.takePicture(context.executor).let {
                                imgFile(it)
                            }
                        }
                    }
                ) {
                    Text("Snap picture")
                }
            }
            LaunchedEffect(previewUseCase) {
                val cameraProvider = context.getCameraProvider()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, camSelector, previewUseCase, imageCaptureUseCase
                    )
                } catch (ex: Exception) {
                    Log.e("fun: CameraCapture", "Could not bind use cases", ex)
                }
            }
        }
    }

}

@Composable
fun CameraPreview(
    modifier : Modifier = Modifier,
    camScale : PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    useCase : (UseCase) -> Unit = {}
) {
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
            useCase(Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            )
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

suspend fun ImageCapture.takePicture(executor : Executor) : File {
    val photoFile = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            File.createTempFile("image", "jpg")
        }.getOrElse { ex ->
            Log.e("fun: ImageCapture.takePicture", "Failed to create temp file", ex)
            File("/dev/null")
        }
    }

    return suspendCoroutine { continuation ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                continuation.resume(photoFile)
            }

            override fun onError(ex: ImageCaptureException) {
                Log.e("fun: ImageCapture.takePicture:suspendCoroutine.takePicture", "Failed to capture image", ex)
                continuation.resumeWithException(ex)
            }
        })
    }
}
