package com.moataz.photoweather.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.moataz.photoweather.R
import com.moataz.photoweather.databinding.ActivityHomeBinding
import com.moataz.photoweather.views.fragment.PhotoListFragment
import com.moataz.photoweather.views.fragment.WelcomeFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.bottomNavigationView.background=null
        binding.photoFAB.setOnClickListener{
            startActivity(Intent(this,PhotoActivity::class.java))
        }

        loadFragment(WelcomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment-> {
                    loadFragment(WelcomeFragment())
                    true
                }
                R.id.photoListFragment -> {
                    loadFragment(PhotoListFragment())
                   true
                }
                else -> false
            }
        }
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.container.id,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}