package com.example.cameraapp

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var gridView : GridView
    private lateinit var imageAdapter : ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        gridView = findViewById(R.id.gridGallery)

        val folderPath = Environment.getExternalStorageDirectory().toString() + "/Android/media/com.example.cameraapp/CameraApp/"
        val folder = File(folderPath)
        val files = folder.listFiles { file -> file.extension == "jpg" || file.extension == "jpeg" }

        imageAdapter = ImageAdapter(this, files.toList())

        gridView.adapter = imageAdapter
    }
}

class ImageAdapter(private val context: Context, private val images: List<File>) : BaseAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = if (convertView == null) {
            // If the view hasn't been created yet, inflate the layout for it
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.grid_item, null) as ImageView
        } else {
            // Otherwise, reuse the existing view
            convertView as ImageView
        }

        // Load the image into the ImageView using Glide (or any other image loading library)
        Glide.with(context)
            .load(images[position])
            .centerCrop()
            .into(imageView)

        return imageView
    }
}
