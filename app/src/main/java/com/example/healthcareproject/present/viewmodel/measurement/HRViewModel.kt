package com.example.healthcareproject.present.viewmodel.measurement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

class HRViewModel @Inject constructor() : ViewModel() {

    private val _bpm = MutableLiveData<Float>()
    val bpm: LiveData<Float> get() = _bpm

    private val _bpmHistory = MutableLiveData<List<Float>>(emptyList())
    val bpmHistory: LiveData<List<Float>> get() = _bpmHistory

    private val maxDataPoints = 20

    init {
        startRealTimeUpdates()
    }

    private fun startRealTimeUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val newBpm = Random.nextFloat() * (120f - 40f) + 40f // Simulate BPM (40-120)
                _bpm.postValue(newBpm)

                // Update history
                val currentHistory = _bpmHistory.value?.toMutableList() ?: mutableListOf()
                currentHistory.add(newBpm)
                if (currentHistory.size > maxDataPoints) {
                    currentHistory.removeAt(0)
                }
                _bpmHistory.postValue(currentHistory)

                delay(1000L) // Update every second
            }
        }
    }
}