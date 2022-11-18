package com.example.climateapp

import android.graphics.Color.*
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.climateapp.ui.theme.ClimateAppTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)

    val colorGrid = Array(20) { IntArray(40) }
    var updater by mutableStateOf(0) //required to force composable re-render

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val getData = DataMaps(baseContext)
        val landMass = getData.getLandData()
        for (i in 0 until colorGrid.size) {
            for (j in 0 until colorGrid[i].size) {
                colorGrid[i][j] = 2
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            ClimateAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RenderMap(landMass, colorGrid)

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun DisplayMap(Array: Array<IntArray>, update: Int) {

        val degree8 = Color(red = 0.60f, green = 0.20f, blue = 0.1f, alpha = 0.5f)
        val degree7 = Color(red = 0.70f, green = 0.20f, blue = 0.15f, alpha = 0.5f)
        val degree6 = Color(red = 0.80f, green = 0.263f, blue = 0.212f, alpha = 0.5f)
        val degree5 = Color(red = 0.957f, green = 0.263f, blue = 0.212f, alpha = 0.5f)
        val degree4 = Color(red = 1.0f, green = 0.341f, blue = 0.133f, alpha = 0.5f)
        val degree3 = Color(red = 1.0f, green = 0.596f, blue = 0.0f, alpha = 0.5f)
        val degree2 = Color(red = 1.0f, green = 0.757f, blue = 0.027f, alpha = 0.5f)
        val degree1 = Color(red = 1.0f, green = 0.922f, blue = 0.231f, alpha = 0.5f)
        val Empty = Color(red = 1.0f, green = 0.922f, blue = 0.231f, alpha = 0f)

        Column() {
            for (i in 0 until Array.size) {
                Row {
                    for (j in 0 until Array[i].size) {
                        //System.out.println(colorGrid[i][j]);
                        when (colorGrid[i][j]) {
                            0 -> ClimateBox(shape = RectangleShape, color = Empty)
                            1 -> ClimateBox(shape = RectangleShape, color = degree1)
                            2 -> ClimateBox(shape = RectangleShape, color = degree2)
                            3 -> ClimateBox(shape = RectangleShape, color = degree3)
                            4 -> ClimateBox(shape = RectangleShape, color = degree4)
                            5 -> ClimateBox(shape = RectangleShape, color = degree5)
                            6 -> ClimateBox(shape = RectangleShape, color = degree6)
                            7 -> ClimateBox(shape = RectangleShape, color = degree7)
                            8 -> ClimateBox(shape = RectangleShape, color = degree8)
                            else -> {
                                ClimateBox(shape = RectangleShape, color = degree8)
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ClimateBox(shape: Shape, color: Color) {
        Column(modifier = Modifier.wrapContentSize(Alignment.Center)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(shape)
                    .background(color)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun RenderMap(landMass: IntArray, Array: Array<IntArray>) {
        ClimateAppTheme {
            startUpdates(Array, landMass)
            DisplayMap(Array, updater)
        }
    }

    val scope = MainScope()
    var job: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun startUpdates(Array: Array<IntArray>, landMass: IntArray) {
        stopUpdates()
        job = scope.launch {
            while (true) {
                updateMap(Array, landMass)
                delay(1000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateMap(Array: Array<IntArray>, landMass: IntArray) {
        var row = 0
        var temperature = 0.0;
        var albedo = 0;
        var sum = 0.0;
        for (i in 0 until Array.size) {
            row = i * 40
            for (j in 0 until Array[i].size) {
                updater++;
                //albedo = landMass[row + j]
                //albedo.toDouble()
                //var albedoDouble = albedo.toDouble()
                //System.out.println(row + j);
                temperature = root(((1-(0.01*landMass[row + j]))*1367)/(4*0.612*.0000000567))
                temperature -= 273.15
                sum += temperature
                //System.out.println(temperature);
                if (landMass[row + j] > 40) {
                    colorGrid[i][j] = 3
                } else if (landMass[row + j] > 30) {
                    colorGrid[i][j] = 5
                } else if (landMass[row + j] > 20) {
                    colorGrid[i][j] = 7
                } else {
                    colorGrid[i][j] = 8
                }
            }
        }
        System.out.println(sum/800);
    }

    fun stopUpdates() {
        job?.cancel()
        job = null
    }

    fun root(a:Double):Double {
        return a.pow(1/4.toDouble())
    }
}