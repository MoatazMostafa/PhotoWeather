package com.moataz.photoweather.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.moataz.photoweather.api.NetworkRepository
import com.moataz.photoweather.databinding.CompPhotoDataBinding
import com.moataz.photoweather.models.CurrentWeather
import com.moataz.photoweather.utils.Constants.ALIGN_LEFT
import com.moataz.photoweather.utils.Constants.FILE_NAME
import com.moataz.photoweather.utils.PhotoData
import com.moataz.photoweather.utils.ViewUtils.drawText
import com.moataz.photoweather.utils.ViewUtils.setPhotoDataView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class PhotoViewModel(application: Application) : AndroidViewModel(application)  {

    private val app = application
    private var _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather>
        get() = _currentWeather

    private var _photoDataList = MutableLiveData<ArrayList<PhotoData>>()
    val photoDataList: LiveData<ArrayList<PhotoData>>
        get() = _photoDataList

    lateinit var filePhoto: File
    lateinit var capturedImage:Bitmap
    var finalImage:MutableLiveData<Bitmap>

    var locationManager: LocationManager? = null
    var uri: Uri?=null

    private val networkRepository = NetworkRepository()
    private var lat:Double = 0.0
    private var lon:Double = 0.0

    init {
        setPhotoFile()
        finalImage = MutableLiveData()
        _photoDataList.value = ArrayList()
    }

    fun setToPhotoDataList(view:CompPhotoDataBinding,title: String, index:Int,inputType: Int = InputType.TYPE_CLASS_TEXT) {
        val photoData = PhotoData("",false, ALIGN_LEFT)
        _photoDataList.value?.add(photoData)
        setPhotoDataView(view,title,index,inputType){ i, text, color, align ->
            _photoDataList.value?.get(i)?.text = text
            _photoDataList.value?.get(i)?.color = color
            _photoDataList.value?.get(i)?.align = align
            finalImage.value = drawText(capturedImage, _photoDataList.value!!)
        }
    }

    private fun setPhotoFile() {
        val storageDirectory = app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        filePhoto = File.createTempFile(FILE_NAME, ".jpg", storageDirectory)
    }

    //Location
     fun getLocation() {
        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }
    }

    private val locationListener: LocationListener = LocationListener { location ->
        lat = location.latitude
        lon = location.longitude
    }

    fun getCurrentWeather() {
        MainScope().launch {
            networkRepository.getCurrentWeather(lat, lon) { currentWeather, errorMsg ->
                if(currentWeather!=null) {
                    _currentWeather.value = currentWeather
                }
                else
                    Toast.makeText(app,errorMsg,Toast.LENGTH_SHORT).show()
            }
        }
    }


}