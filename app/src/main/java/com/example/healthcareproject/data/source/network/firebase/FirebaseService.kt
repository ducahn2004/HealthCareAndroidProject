package com.example.healthcareproject.data.source.network.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseService {
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    fun getReference(path: String): DatabaseReference {
        return database.getReference(path)
    }

    fun getChildReference(parentPath: String, childPath: String): DatabaseReference {
        return database.getReference(parentPath).child(childPath)
    }
}