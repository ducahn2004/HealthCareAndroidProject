package com.example.healthcareproject.presentation.util

import androidx.databinding.Observable
import java.lang.reflect.Field

/**
 * Extension function to access the observers/callbacks in an Observable
 * This is needed because the Observable class doesn't provide direct access to its callbacks
 */
fun Observable.getOnPropertyChangedCallbacks(): List<Observable.OnPropertyChangedCallback> {
    return try {
        // Use reflection to access the private mCallbacks field in Observable
        val field: Field = Observable::class.java.getDeclaredField("mCallbacks")
        field.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val callbacks = field.get(this) as? ArrayList<Observable.OnPropertyChangedCallback>
        callbacks?.toList() ?: emptyList()
    } catch (e: Exception) {
        // In case of any reflection errors, return an empty list
        emptyList()
    }
}

/**
 * Extension function to find and get a callback from an Observable
 * Returns a non-null callback or creates a dummy empty one if not found
 */
fun Observable.findCallback(predicate: (Observable.OnPropertyChangedCallback) -> Boolean): Observable.OnPropertyChangedCallback {
    return getOnPropertyChangedCallbacks().find(predicate) ?: object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            // Empty dummy implementation
        }
    }
}

