package com.example.runmedia_prueba

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.Location
import android.os.Bundle
import android.widget.Chronometer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.runmedia_prueba.ui.theme.Runmedia_pruebaTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import java.util.*

class MainActivity : ComponentActivity(){

    val run = RunViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Runmedia_pruebaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting(this)

                }
            }
        }
    }

}

fun cogerCoordenada(activity: Activity, viewModel: RunViewModel, context: Context) {

    lateinit var fusedLocationClient: FusedLocationProviderClient

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

    var longitud = 0.0
    var latitud = 0.0


    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            Toast.makeText(context, location.toString(), Toast.LENGTH_SHORT).show()

            longitud = location?.longitude?.toDouble()!!
            latitud = location.latitude.toDouble()

            var punto = LatLng(latitud, longitud)

            viewModel.puntoEvent(RunContract.InsertarPunto.InsertPunto(punto))
            viewModel.puntoEvent(RunContract.InsertarPunto.OnPuntoChange(viewModel.lastLocationCoordinates))
        }
}

@Composable
fun Greeting(activity: Activity, viewModel: RunViewModel = hiltViewModel()) {

    val context = LocalContext.current

    var properties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true,
            )
        )
    }

    var uiSettings by remember {
        mutableStateOf(MapUiSettings(myLocationButtonEnabled = true, mapToolbarEnabled = true))
    }

    val puntos = viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Blue)
                .weight(1f)
                .padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            GoogleMap(properties = properties, uiSettings = uiSettings) {
                //viewModel.handleEvent(RunContract.Event.PuntosCardinales)

                if (!viewModel.carrera) {
                    Polyline(points = puntos.value.puntos)
                }

            }
            Column {

                Button(onClick = {
                    properties = properties.copy(
                        isBuildingEnabled = !properties.isBuildingEnabled
                    )
                    viewModel.cambiarBooleanCarrera(RunContract.EmpezarCarrera.BooleanTerminarCarrera)
                }) {

                    if (!viewModel.carrera) {

                        Toast.makeText(context, "CARRERA TERMINADA", Toast.LENGTH_SHORT).show()

                    }

                    Text(text = "STOP RUN")
                }

                Button(onClick = {

                    properties = properties.copy(
                        isBuildingEnabled = !properties.isBuildingEnabled
                    )

                    loadTimer(activity, viewModel, context)

                }) {

                    Text(text = "EMPEZAR CARRERITA")
                }


            }

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .weight(1f)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {

        }

    }

}

private fun loadTimer(activity: Activity, viewModel: RunViewModel, context: Context) {

    val timer = Timer()

    val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            if (viewModel.carrera) {
                cogerCoordenada(activity, viewModel, context)
            } else {
                timer.cancel()
            }
        }

    }

    timer.schedule(timerTask, 0L, 2000)

}

