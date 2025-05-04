package com.example.healthcareproject.present.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthcareproject.present.pill.Medication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AlarmViewModel : ViewModel() {

    private val _alarms = MutableLiveData<List<Alarm>>(emptyList())
    val alarms: LiveData<List<Alarm>> get() = _alarms

    private val _medications = MutableLiveData<List<Medication>>(emptyList())
    val medications: LiveData<List<Medication>> get() = _medications

    fun loadAlarms(context: Context) {
        val sharedPrefs = context.getSharedPreferences("alarms", Context.MODE_PRIVATE)
        val alarmsJson = sharedPrefs.getString("alarm_list", null)
        if (alarmsJson != null) {
            val type = object : TypeToken<List<Alarm>>() {}.type
            val loadedAlarms: List<Alarm> = Gson().fromJson(alarmsJson, type)
            _alarms.value = loadedAlarms
        }
    }

    fun loadMedications(context: Context) {
        val sharedPrefs = context.getSharedPreferences("medications", Context.MODE_PRIVATE)
        val medicationsJson = sharedPrefs.getString("medication_list", null)
        if (medicationsJson != null) {
            val type = object : TypeToken<List<Medication>>() {}.type
            val loadedMedications: List<Medication> = Gson().fromJson(medicationsJson, type)
            _medications.value = loadedMedications
        }
    }

    fun addAlarm(alarm: Alarm, context: Context) {
        val currentAlarms = _alarms.value?.toMutableList() ?: mutableListOf()
        currentAlarms.add(alarm)
        _alarms.value = currentAlarms

        // Save to SharedPreferences
        val sharedPrefs = context.getSharedPreferences("alarms", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val alarmsJson = Gson().toJson(currentAlarms)
        editor.putString("alarm_list", alarmsJson)
        editor.apply()
    }
}