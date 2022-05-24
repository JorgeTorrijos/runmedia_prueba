package com.example.runmedia_prueba

import com.google.android.gms.maps.model.LatLng

interface RunContract {

    sealed class Event {
        object PuntosCardinales : Event()
    }

    sealed class InsertarPunto {
        data class InsertPunto(val punto: LatLng): InsertarPunto()
        data class OnPuntoChange(val punto: LatLng): InsertarPunto()
    }

    sealed class EmpezarCarrera{
        object BooleanEmpezarCarrera: EmpezarCarrera()
        object BooleanTerminarCarrera: EmpezarCarrera()
    }

    data class State(val puntos: List<LatLng> = emptyList())

}

