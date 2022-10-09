package com.moataz.photoweather.viewModels

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File

class ListViewModel(application: Application) : AndroidViewModel(application)  {
    val app  = application
    private var _photoList=MutableLiveData<List<File>>()
    val photoList:LiveData<List<File>>
        get() = _photoList


    fun getPhotoListFromFolder() {
        val cw = ContextWrapper(app.applicationContext)
        val directory = cw.getDir("WeatherPhotos", Context.MODE_PRIVATE)
        _photoList.value = directory.listFiles()?.toList()
    }
}