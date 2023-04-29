package com.example.cameraapp

import android.view.ViewGroup
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoilApi
@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
@Composable
fun MainCameraPreview(modifier : Modifier = Modifier) {
    var imageUri by remember { mutableStateOf(EMPTY_IMG_URI) }
    if (imageUri != EMPTY_IMG_URI) {
        Box(modifier = modifier) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberImagePainter(imageUri),
                contentDescription = "Captured image"
            )
            Button(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    imageUri = EMPTY_IMG_URI
                }
            ) {
                Text("Go back")
            }
        }
    } else {
        var isGallerySelected by remember { mutableStateOf(false) }
        if (isGallerySelected) {
            Gallery(
                modifier = modifier,
                imgUri = { uri ->
                    isGallerySelected = false
                    imageUri = uri
                }
            )
        } else {
            Box(modifier = modifier) {
                CameraCapture(
                    modifier = modifier,
                    imgFile = { file ->
                        imageUri = file.toUri()
                    }
                )
                Button(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp),
                    onClick = {
                        isGallerySelected = true
                    }
                ) {
                    Text("Select from Gallery")
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
            useCase(
                Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            )
            previewView
        }
    )
}