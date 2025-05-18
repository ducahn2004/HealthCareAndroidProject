package com.example.healthcareproject.presentation.util

import androidx.databinding.Observable
import java.lang.reflect.Field

fun Observable.getOnPropertyChangedCallbacks(): List<Observable.OnPropertyChangedCallback> {
    return try {
        val field: Field = Observable::class.java.getDeclaredField("mCallbacks")
        field.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val callbacks = field.get(this) as? ArrayList<Observable.OnPropertyChangedCallback>
        callbacks?.toList() ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

fun Observable.findCallback(predicate: (Observable.OnPropertyChangedCallback) -> Boolean): Observable.OnPropertyChangedCallback {
    return getOnPropertyChangedCallbacks().find(predicate) ?: object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        }
    }
}

