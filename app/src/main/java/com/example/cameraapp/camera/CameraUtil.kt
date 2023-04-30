package com.example.cameraapp.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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
            File.createTempFile("image", ".jpg")
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