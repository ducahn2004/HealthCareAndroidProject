package com.example.healthcareproject.setting

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPrefsHelper {
    private const val PREFS_NAME = "emergency_contacts_prefs"
    private const val KEY_CONTACTS = "emergency_contacts"

    fun saveEmergencyContacts(context: Context, contacts: List<EmergencyContact>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(contacts)
        editor.putString(KEY_CONTACTS, json)
        editor.apply()
    }

    fun getEmergencyContacts(context: Context): List<EmergencyContact> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CONTACTS, null)
        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<EmergencyContact>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
}