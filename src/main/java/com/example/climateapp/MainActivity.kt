package com.example.climateapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.climateapp.ui.theme.ClimateAppTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClimateAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    DefaultPreview()
                }
            }
        }
    }
}

@Composable
fun Greeting(Array: Array<IntArray>) {
    Column() {
        for (i in 0 until Array.size) {
            Row {
                for (j in 0 until Array[i].size) {
                    if (Array[i][j] == 2) {
                        ExampleBox(shape = RectangleShape, color = Red)
                    }
                    else{
                        ExampleBox(shape = RectangleShape, color = Blue)
                    }
                }
            }
        }
    }
}

@Composable
fun ExampleBox(shape: Shape, color: androidx.compose.ui.graphics.Color){
    Column(modifier = Modifier.wrapContentSize(Alignment.Center)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(shape)
                .background(color)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val colorGrid = Array(10) { IntArray(20) }
    for (i in 0 until colorGrid.size) {
        for (j in 0 until colorGrid[i].size) {
            colorGrid[i][j] = 2 //just an example
        }
    }

    ClimateAppTheme {
        startUpdates(colorGrid)
        Greeting(colorGrid)
    }
}
val scope = MainScope() // could also use an other scope such as viewModelScope if available
var job: Job? = null

fun startUpdates(Array: Array<IntArray>) {
    stopUpdates()
    job = scope.launch {
        while(true) {
            updateMap(Array)
            delay(1000)
        }
    }
}

fun updateMap(Array: Array<IntArray>) {
    for (i in 0 until Array.size) {
        for (j in 0 until Array[i].size) {
            Array[i][j] = 2 //just an example
        }
    }
}

fun stopUpdates() {
    job?.cancel()
    job = null
}