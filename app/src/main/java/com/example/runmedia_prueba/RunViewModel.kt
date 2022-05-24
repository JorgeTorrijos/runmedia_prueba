package com.example.runmedia_prueba

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RunViewModel : ViewModel() {

    var puntos = mutableListOf<LatLng>()
        private set

    var lastLocationCoordinates =  LatLng(0.0,0.0)
        private set

    var carrera = true
        private set

    private val _uiState: MutableStateFlow<RunContract.State> by lazy {
        MutableStateFlow(RunContract.State())
    }
    val uiState: StateFlow<RunContract.State> = _uiState

    private val _uiError = Channel<String>()
    val uiError = _uiError.receiveAsFlow()

    fun handleEvent(event: RunContract.Event) {
        when (event) {
            is RunContract.Event.PuntosCardinales -> {
                viewModelScope.launch{
                    _uiState.update { it.copy(puntos) }
                }
            }
        }
    }

    fun puntoEvent(event: RunContract.InsertarPunto) {
        when (event) {
            is RunContract.InsertarPunto.OnPuntoChange -> {
                viewModelScope.launch {
                    lastLocationCoordinates = event.punto
                }
            }
            is RunContract.InsertarPunto.InsertPunto -> {
                viewModelScope.launch {
                    lastLocationCoordinates = event.punto
                    puntos.add(lastLocationCoordinates)
                    _uiState.update { it.copy(puntos) }
                }

            }
        }
    }

    fun cambiarBooleanCarrera(event: RunContract.EmpezarCarrera) {
        when(event){
            is RunContract.EmpezarCarrera.BooleanEmpezarCarrera -> {
                viewModelScope.launch {
                    carrera = true
                }
            }
            is RunContract.EmpezarCarrera.BooleanTerminarCarrera -> {
                viewModelScope.launch {
                    carrera = false
                }
            }
        }
    }

}