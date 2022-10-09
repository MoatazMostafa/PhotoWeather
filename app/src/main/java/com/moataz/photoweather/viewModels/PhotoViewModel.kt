package com.moataz.photoweather.viewModels

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.lifecycle.viewModelScope
import com.moataz.photoweather.api.NetworkRepository
import com.moataz.photoweather.databinding.CompPhotoDataBinding
import com.moataz.photoweather.models.CurrentWeather
import com.moataz.photoweather.utils.Constants.ALIGN_LEFT
import com.moataz.photoweather.utils.Constants.FILE_NAME
import com.moataz.photoweather.utils.Constants.FILE_PROVIDER_PATH
import com.moataz.photoweather.utils.PhotoData
import com.moataz.photoweather.utils.ViewUtils
import com.moataz.photoweather.utils.ViewUtils.drawText
import com.moataz.photoweather.utils.ViewUtils.setPhotoDataView
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class PhotoViewModel(application: Application) : AndroidViewModel(application)  {

    private val app = application
    private var _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather>
        get() = _currentWeather

    private var photoDataList = ArrayList<PhotoData>()

    lateinit var filePhoto: File
    var capturedImage:MutableLiveData<Bitmap>
    var finalImage:MutableLiveData<Bitmap>

    var locationManager: LocationManager? = null
    var uri: Uri?=null

    private val networkRepository = NetworkRepository()
    private var lat:Double = 0.0
    private var lon:Double = 0.0

    init {
        setPhotoFile()
        finalImage = MutableLiveData()
        capturedImage = MutableLiveData()
    }

    fun getCurrentWeather() {
        viewModelScope.launch {
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
            if(capturedImage.value!=null)
                finalImage.value = drawText(capturedImage.value!!, photoDataList)
        }
    }

    private fun setPhotoFile() {
        val storageDirectory = app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        filePhoto = File.createTempFile(FILE_NAME, ".jpg", storageDirectory)
    }

    fun getShareUri(context: Context): Uri? {
        var uri: Uri? = null
        try {
            val outputStream = FileOutputStream(filePhoto.absoluteFile)
            finalImage.value?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = FileProvider.getUriForFile(context, FILE_PROVIDER_PATH, filePhoto)
        } catch (_: Exception) { }
        return uri
    }

    fun saveImage(){
        val cw = ContextWrapper(app.applicationContext)
        val directory = cw.getDir("WeatherPhotos", Context.MODE_PRIVATE)
        val file = File(directory, "${UUID.randomUUID()}.jpg")
        if (!file.exists()) {
            val fos: FileOutputStream?
            try {
                fos = FileOutputStream(file)
                finalImage.value?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                Toast.makeText(app,"Photo Saved",Toast.LENGTH_LONG).show()
            } catch (_: IOException) { }
        }
    }

    //Location
     fun getLocation() {
        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }
    }

    fun setCapturedImage() {
        val bitmap = BitmapFactory.decodeFile(filePhoto.absolutePath)
        capturedImage.value = ViewUtils.setImageOrientation(bitmap, filePhoto.absolutePath) ?:bitmap
        finalImage.value = capturedImage.value
        getCurrentWeather()
    }

    private val locationListener: LocationListener = LocationListener { location ->
        lat = location.latitude
        lon = location.longitude
    }
}