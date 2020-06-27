package com.madfooatcom.testlibrary

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson

object GsonHandler {
    fun toGson(context: Context, apiVolume: ApiVolume): String {
        val string: String = Gson().toJson(apiVolume)
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
        return string
    }
}