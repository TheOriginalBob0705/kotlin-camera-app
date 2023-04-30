package com.example.cameraapp.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
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
import java.io.File
import java.io.FileOutputStream

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
                        .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()
                )
            }
            Box {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    useCase = { previewUseCase = it }
                )
                CameraButton(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(20.dp)
                        .align(Alignment.BottomCenter),
                    onClick = {
                        corScope.launch {
                            imageCaptureUseCase.takePicture(context.executor).let {
                                imgFile(it)
                                val bitmap = BitmapFactory.decodeFile(it.absolutePath)
                                saveImageToCameraRoll(bitmap, context)
                                /* TODO: Upload image to server */
                            }
                        }
                    }
                )
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

private fun saveImageToCameraRoll(bitmap: Bitmap, context: Context) {
    val filename = "${System.currentTimeMillis()}.jpg"
    val mediaStorageDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "Camera"
    )

    if (!mediaStorageDir.exists()) {
        mediaStorageDir.mkdirs()
    }

    val file = File(mediaStorageDir, filename)
    val outStream = FileOutputStream(file)

    val rotatedBitmap = bitmap.rotate(90f)

    // Save the rotated bitmap to file
    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
    outStream.flush()
    outStream.close()

    // Tell the media scanner about the new file so that it is immediately available to the user.
    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    mediaScanIntent.data = Uri.fromFile(file)
    context.sendBroadcast(mediaScanIntent)
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
