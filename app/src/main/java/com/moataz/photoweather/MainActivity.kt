package com.moataz.photoweather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.moataz.photoweather.views.HomeActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MainScope().launch {
            toHome()
        }
    }

    private suspend fun toHome() {
        delay(2000)
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        finish()
    }

}