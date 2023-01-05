package com.example.climateapp

import android.graphics.Color.*
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
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
    val dataArray = Array(20) { Array(40) { DoubleArray(4) } }

    var solarConst = 1367.00 //solar radiation in w/m^2
    var CO2 = 0.00
    var boltzmann = .0000000567 //Stefan–Boltzmann constant
    var doOnce = true;
    var isWide = true;

    var updater by mutableStateOf(0) //required to force composable re-render
    //enum class WindowSizeClass { COMPACT, MEDIUM, EXPANDED }

    val scope = MainScope()
    var job: Job? = null

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            var row = 0
            var temperature = 0.0;
            val bitMapData = DataMaps(baseContext)
            val landMassTemp = bitMapData.getLandData()
            val iceCoverageTemp = bitMapData.getIceData()
            var sum = 0.0;

            stopUpdates()
            System.out.println("this----------------------------------------------------------------");
            for (i in 0 until dataArray.size) {
                row = i * 40
                for (j in 0 until dataArray[i].size) {
                    temperature =
                        root(((1 - (0.01 * landMassTemp[row + j]) - (0.02 * iceCoverageTemp[row + j])) * solarConst) / (4 * 0.612 * boltzmann))
                    temperature -= 273.15
                    sum += temperature
                    dataArray[i][j][0] = temperature
                    dataArray[i][j][1] = temperature
                    dataArray[i][j][2] = landMassTemp[row + j].toDouble()
                    dataArray[i][j][3] = iceCoverageTemp[row + j].toDouble()
                }
            }
            System.out.println("-------------------------------------" + sum / 800);

            setContent {
                val windowSizeClass = calculateWindowSizeClass(this)
                ClimateAppTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        RenderMap(windowSizeClass)
                    }
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun DisplayMap(update: Int) {

        val degree8 = Color(red = 0.675f, green = 0.161f, blue = 0.0f, alpha = 1.0f)
        val degree7 = Color(red = 1.0f, green = 0.341f, blue = 0.133f, alpha = 1.0f)
        val degree6 = Color(red = 1.0f, green = 0.467f, blue = 0.0f, alpha = 1.0f)
        val degree5 = Color(red = 1.0f, green = 0.675f, blue = 0.027f, alpha = 1.0f)
        val degree4 = Color(red = 1.0f, green = 0.675f, blue = 0.027f, alpha = 1.0f)
        val degree3 = Color(red = 1.0f, green = 0.808f, blue = 0.231f, alpha = 1.0f)
        val degree2 = Color(red = 1.0f, green = 0.922f, blue = 0.231f, alpha = 1.0f)
        val degree1 = Color(red = 1.0f, green = 0.949f, blue = 0.502f, alpha = 1.0f)
        val empty = Color(red = 1.0f, green = 1.0f, blue = 1.0f, alpha = 0.0f)

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()){
            for (i in 0 until colorGrid.size) {
                Row {
                    for (j in 0 until colorGrid[i].size) {
                        //System.out.println(colorGrid[i][j]);
                        when (colorGrid[i][j]) {
                            0 -> ClimateBox(shape = RectangleShape, color = empty)
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
        var boxSize = 10;
        if(isWide)
            boxSize = 16;
        Column(modifier = Modifier.wrapContentSize(Alignment.Center)) {
            Box(
                modifier = Modifier
                    .size(boxSize.dp)
                    .clip(shape)
                    .background(color)
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun RenderMap(windowSizeClass: WindowSizeClass) {
        ClimateAppTheme {
            var iceLevel = rememberSaveable { mutableStateOf( 100 ) }
            var isRunning = rememberSaveable { mutableStateOf( false ) }
            var sheetHeight = 500
            isWide = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

            if(isWide)
                sheetHeight = 20

            //add bottom drawer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //DisplayMap(updater)

                BottomSheetScaffold(
                    sheetContent = {
                        // Sheet content

                        Row(modifier = Modifier
                            .padding(all = Dp(10F))
                            .align(Alignment.CenterHorizontally)){
                            Button(
                                onClick = {
                                    System.out.println("----------------------------------------------------------------------------------------YESS")
                                    startUpdates(iceLevel)
                                    isRunning.value = true
                                },
                                modifier = Modifier.padding(all = Dp(10F)),
                                enabled = true,
                                border = BorderStroke(width = 1.dp, brush = SolidColor(Color.Black)),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Magenta)
                            )
                            {
                                Text(text = "Start", color = Color.White)
                            }
                            Button(
                                onClick = {
                                    stopUpdates()
                                    isRunning.value = false
                                },
                                modifier = Modifier.padding(all = Dp(10F)),
                                enabled = true,
                                border = BorderStroke(width = 1.dp, brush = SolidColor(Color.Black)),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
                            )
                            {
                                Text(text = "Stop", color = Color.White)
                            }
                        }
                        var sliderPosition by rememberSaveable { mutableStateOf(0f) }
                        Text(text = sliderPosition.toString())
                        Slider(value = sliderPosition, onValueChange = { sliderPosition = it })

                    },
                    // Defaults to BottomSheetScaffoldDefaults.SheetPeekHeight
                    sheetPeekHeight = sheetHeight.dp,
                    // Defaults to true
                    sheetGesturesEnabled = true,
                    modifier = Modifier.fillMaxWidth()

                ) {
                    // Screen content
                    DisplayMap(updater)
                    if(isRunning.value){
                        startUpdates(iceLevel)

                    }
                    else{
                        updateValues(iceLevel)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startUpdates(iceLevel: MutableState<Int>) {
        stopUpdates()
        job = scope.launch {
            while (true) {
                iceLevel.value -= 1
                updateValues(iceLevel)
                delay(1000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateValues(iceLevel: MutableState<Int>) {
        var row = 0
        var temperature = 0.0;
        var albedo = 0;
        var sum = 0.0;
        for (i in 0 until dataArray.size) {
            row = i * 40
            for (j in 0 until dataArray[i].size) {
                updater++;
                temperature = root(((1-(0.01*dataArray[i][j][2])-((0.0002*iceLevel.value)*dataArray[i][j][3]))*solarConst)/(4*0.612*boltzmann))
                temperature -= 273.15
                dataArray[i][j][1] = temperature
                //temperature = root(((1-(0.01*landMass[row + j]))*1367)/(4*0.612*.0000000567))
                //sum += temperature
                //System.out.println(iceLevel);
                if (temperature-dataArray[i][j][0] > 8) {
                    colorGrid[i][j] = 8
                } else if (temperature-dataArray[i][j][0] > 7) {
                    colorGrid[i][j] = 7
                } else if (temperature-dataArray[i][j][0] > 6) {
                    colorGrid[i][j] = 6
                } else if (temperature-dataArray[i][j][0] > 5) {
                    colorGrid[i][j] = 5
                } else if (temperature-dataArray[i][j][0] > 4) {
                    colorGrid[i][j] = 4
                } else if (temperature-dataArray[i][j][0] > 3) {
                    colorGrid[i][j] = 3
                } else if (temperature-dataArray[i][j][0] > 2) {
                    colorGrid[i][j] = 2
                } else if (temperature-dataArray[i][j][0] > 1) {
                    colorGrid[i][j] = 1
                } else {
                    colorGrid[i][j] = 0
                }
            }
        }
        //System.out.println(sum/800);
    }

    fun stopUpdates() {
        job?.cancel()
        job = null
    }

    fun root(a:Double):Double {
        return a.pow(1/4.toDouble())
    }
}