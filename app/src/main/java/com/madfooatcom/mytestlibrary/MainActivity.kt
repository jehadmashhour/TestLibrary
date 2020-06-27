package com.madfooatcom.mytestlibrary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.madfooatcom.testlibrary.ApiVolume
import com.madfooatcom.testlibrary.GsonHandler


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val apiVolume = ApiVolume("login", "Jehad", 4)
        GsonHandler.toGson(this, apiVolume)
    }
}
