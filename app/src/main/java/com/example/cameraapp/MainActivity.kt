package com.example.cameraapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var imgPicSave : ImageView
    lateinit var btnTakePic : Button
    val REQUEST_IMAGE_CAPTURE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgPicSave = findViewById(R.id.imgPicSave)
        btnTakePic = findViewById(R.id.btnTakePic)

        btnTakePic.setOnClickListener {
            val picIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try {
                startActivityForResult(picIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e : ActivityNotFoundException) {
                Toast.makeText(this, "Error: " + e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            imgPicSave.setImageBitmap(imgBitmap)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}