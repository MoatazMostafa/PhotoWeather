package com.moataz.photoweather.views

import android.Manifest
import android.content.Intent
import android.graphics.*
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.moataz.photoweather.R
import com.moataz.photoweather.databinding.ActivityPhotoBinding
import com.moataz.photoweather.utils.ViewUtils.disablePhotoDataView
import com.moataz.photoweather.utils.ViewUtils.enablePhotoDataView
import com.moataz.photoweather.utils.ViewUtils.setImageOrientation
import com.moataz.photoweather.viewModels.PhotoViewModel

class PhotoActivity : AppCompatActivity() {
    private val viewModel: PhotoViewModel by lazy { ViewModelProvider(this)[PhotoViewModel::class.java] }

    private lateinit var binding: ActivityPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setDataFields()
        locationRequestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        binding.photoFrameView.setOnClickListener { cameraRequestPermission.launch(Manifest.permission.CAMERA) }
        viewModel.finalImage.observe(this) { setImageView() }
        viewModel.currentWeather.observe(this){ data ->
            binding.temperatureDataField.photoInputEditText.setText(data.main.temp_max.toString())
            binding.weatherConditionDataField.photoInputEditText.setText(data.weather.first().description)
            binding.placeDataField.photoInputEditText.setText(data.name)
            enableView()
        }
        binding.photoActionBar.backImageView.setOnClickListener{
            this.finish()
        }
        binding.photoActionBar.shareImageView

    }

    private fun setDataFields() {
        viewModel.setToPhotoDataList(binding.temperatureDataField,getString(R.string.temperature),0, InputType.TYPE_CLASS_NUMBER)
        viewModel.setToPhotoDataList(binding.weatherConditionDataField,getString(R.string.weather_condition),1)
        viewModel.setToPhotoDataList(binding.placeDataField,getString(R.string.place),2)
        viewModel.setToPhotoDataList(binding.customDataField,getString(R.string.feel),3)
        disableView()
    }

    private fun enableView(){
        enablePhotoDataView(binding.temperatureDataField)
        enablePhotoDataView(binding.weatherConditionDataField)
        enablePhotoDataView(binding.placeDataField)
        enablePhotoDataView(binding.customDataField)
    }

    private fun disableView(){
        disablePhotoDataView(binding.temperatureDataField)
        disablePhotoDataView(binding.weatherConditionDataField)
        disablePhotoDataView(binding.placeDataField)
        disablePhotoDataView(binding.customDataField)
    }

    private fun setImageView(){
        binding.capturedPhotoImageView.load(viewModel.finalImage.value)
        binding.capturedPhotoImageView.scaleType = ImageView.ScaleType.FIT_XY
    }

    private val cameraRequestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            viewModel.uri = FileProvider.getUriForFile(this, "com.moataz.photoweather.file_provider", viewModel.filePhoto)
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, viewModel.uri)
            camera.launch(takePhotoIntent)
        }
    }

    private val locationRequestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            viewModel.locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
            viewModel.getLocation()
        }
    }

    private val camera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.photoFrameView.isEnabled = false
            binding.clickToTakePhotoTV.visibility = View.GONE
            val bitmap = BitmapFactory.decodeFile(viewModel.filePhoto.absolutePath)
            viewModel.capturedImage = setImageOrientation(bitmap, viewModel.filePhoto.absolutePath)?:bitmap
            viewModel.finalImage.value = viewModel.capturedImage
            viewModel.getCurrentWeather()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uri!=null) {
            setImageView()
        }
    }

}