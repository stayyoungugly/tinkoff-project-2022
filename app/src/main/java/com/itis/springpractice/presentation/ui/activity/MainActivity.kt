package com.itis.springpractice.presentation.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.itis.springpractice.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment)
            .navController
    }
}
