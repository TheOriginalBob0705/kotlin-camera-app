package com.example.cameraapp

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.apache.http.client.methods.HttpGet
import java.io.File
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients

@ExperimentalCoroutinesApi
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
                Button(
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
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
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

