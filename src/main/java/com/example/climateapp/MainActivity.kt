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


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val getData = DataMaps(baseContext)
        val landMass = getData.getLandData()

        super.onCreate(savedInstanceState)
        setContent {
            ClimateAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    RenderMap(landMass)

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
    fun DisplayMap(Array: Array<IntArray>) {

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
                    when (Array[i][j]) {
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
fun ClimateBox(shape: Shape, color: Color){
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
fun RenderMap(landMass: IntArray) {
    val colorGrid = Array(10) { IntArray(20) }
    for (i in 0 until colorGrid.size) {
        for (j in 0 until colorGrid[i].size) {
            colorGrid[i][j] = 2 //just an example
        }
    }

    ClimateAppTheme {
        startUpdates(colorGrid, landMass)
        DisplayMap(colorGrid)
    }
}
val scope = MainScope() // could also use an other scope such as viewModelScope if available
var job: Job? = null

fun startUpdates(Array: Array<IntArray>, landMass: IntArray) {
    stopUpdates()
    job = scope.launch {
        while(true) {
            updateMap(Array, landMass)
            delay(1000)
        }
    }
}

fun updateMap(Array: Array<IntArray>, landMass: IntArray) {
    for (i in 0 until Array.size) {
        for (j in 0 until Array[i].size) {
            Array[i][j] = 2 //just an example
        }
    }
    //System.out.println("test");
}

fun stopUpdates() {
    job?.cancel()
    job = null
}