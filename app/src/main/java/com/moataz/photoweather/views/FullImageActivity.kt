package com.moataz.photoweather.views

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.moataz.photoweather.databinding.ActivityFullImageBinding
import com.moataz.photoweather.utils.ViewUtils

class FullImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullImageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val photoPath:String = this.intent.extras?.getString("PhotoFile")?:""
        val bitmap = BitmapFactory.decodeFile(photoPath)
        val rotatedBitmap = ViewUtils.setImageOrientation(bitmap,photoPath) ?:bitmap
        binding.photoImageView.setImageBitmap(rotatedBitmap)
    }
}