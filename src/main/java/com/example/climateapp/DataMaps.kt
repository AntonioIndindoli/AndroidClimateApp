package com.example.climateapp

import com.example.climateapp.R
import android.content.Context
import android.graphics.BitmapFactory
import java.security.AccessController

class DataMaps (context : Context){
    val icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.test)
    val pixels = IntArray(icon.width * icon.height)
    fun makeArray (){
        icon.getPixels(pixels, 0, 0, 0, 0, icon.width, icon.height)
    }
}