package com.moataz.photoweather.views

import android.Manifest
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.RoundedCornersTransformation
import com.moataz.photoweather.R
import com.moataz.photoweather.databinding.ActivityPhotoBinding
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
        binding.photoFrameV.setOnClickListener {
            cameraRequestPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setDataFields() {
        viewModel.setToPhotoDataList(binding.temperatureDataField,getString(R.string.temperature),0)
        viewModel.setToPhotoDataList(binding.weatherConditionDataField,getString(R.string.weather_condition),1)
        viewModel.setToPhotoDataList(binding.placeDataField,getString(R.string.place),2)
        viewModel.setToPhotoDataList(binding.customDataField,getString(R.string.feel),3)
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
                viewModel.getCurrentWeather()
                binding.capturedPhotoImageView.load(viewModel.uri){
                    transformations(RoundedCornersTransformation(8f))
                }
                binding.photoFrameV.isEnabled = false
            }
        }

    override fun onResume() {
        super.onResume()
        if (viewModel.uri!=null)
            binding.capturedPhotoImageView.load(viewModel.uri){
                transformations(RoundedCornersTransformation(8f))
            }
    }


}