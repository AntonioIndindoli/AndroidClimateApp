package com.example.climateapp

import com.example.climateapp.R
import android.content.Context
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.graphics.Color.*
import java.security.AccessController

class DataMaps (context : Context){
    var landMass = BitmapFactory.decodeResource(context.getResources(), R.drawable.albedo)

    fun getLandData (): IntArray {
        landMass = createScaledBitmap(landMass, 40, 20, false)
        val pixels = IntArray(landMass.width * landMass.height)
        landMass.getPixels(pixels, 0, landMass.width, 0, 0, landMass.width, landMass.height)

        var sum = 0;
        for (i in 0 until pixels.size) {
            pixels[i] = green(pixels[i]);
            sum += pixels[i];
            System.out.println(pixels[i]);
        }
        System.out.println(sum/800);
        return pixels
    }
}