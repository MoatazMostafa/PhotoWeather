package com.moataz.photoweather.viewModels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
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
import java.io.FileOutputStream


class PhotoViewModel(application: Application) : AndroidViewModel(application)  {

    private val app = application
    private var _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather>
        get() = _currentWeather

    private var photoDataList = ArrayList<PhotoData>()

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

    fun setToPhotoDataList(view:CompPhotoDataBinding,title: String, index:Int,inputType: Int = InputType.TYPE_CLASS_TEXT) {
        val photoData = PhotoData("",false, ALIGN_LEFT)
        photoDataList.add(photoData)
        setPhotoDataView(view,title,index,inputType){ i, text, color, align ->
            photoDataList[i].text = text
            photoDataList[i].color = color
            photoDataList[i].align = align
            finalImage.value = drawText(capturedImage, photoDataList)
        }
    }

    private fun setPhotoFile() {
        val storageDirectory = app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        filePhoto = File.createTempFile(FILE_NAME, ".jpg", storageDirectory)
    }

    fun getShareUri(context: Context): Uri? {
        var uri: Uri? = null
        try {
            filePhoto.mkdirs()
            val outputStream = FileOutputStream(filePhoto.absoluteFile)
            finalImage.value?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = FileProvider.getUriForFile(context, "com.moataz.photoweather.file_provider", filePhoto)
        } catch (_: Exception) { }
        return uri
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
}