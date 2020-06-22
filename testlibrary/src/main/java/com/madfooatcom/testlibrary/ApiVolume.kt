package com.madfooatcom.testlibrary

import com.google.gson.annotations.SerializedName

data class ApiVolume(
    @SerializedName("name") var name: String,
    @SerializedName("publisher") var apiPublisher: String,
    @SerializedName("image") var apiImage: Int
)