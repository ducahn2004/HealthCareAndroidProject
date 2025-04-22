package com.example.healthcareproject.present

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcareproject.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    private lateinit var editTextUserId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextDateOfBirth: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerBloodType: Spinner
    private lateinit var buttonSaveUser: Button
    private lateinit var textViewUsers: TextView

    private val userViewModel: UserViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Initialize UI components
        editTextUserId = findViewById(R.id.editTextUserId)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextName = findViewById(R.id.editTextName)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth)
        spinnerGender = findViewById(R.id.spinnerGender)
        spinnerBloodType = findViewById(R.id.spinnerBloodType)
        buttonSaveUser = findViewById(R.id.buttonSaveUser)
        textViewUsers = findViewById(R.id.textViewUsers)

        // Set up adapters for spinners
        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        val bloodTypeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.blood_type_options,
            android.R.layout.simple_spinner_item
        )
        bloodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBloodType.adapter = bloodTypeAdapter

        // Observe ViewModel LiveData
        userViewModel.user.observe(this) { user ->
            user?.let {
                textViewUsers.text = "ID: ${it.userId}, Name: ${it.name}, Phone: ${it.phone}"
            }
        }

        userViewModel.error.observe(this) { error ->
            error?.let {
                textViewUsers.text = "Error: $it"
            }
        }

        // Set up button click listener
        buttonSaveUser.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        val userId = editTextUserId.text.toString()
        val password = editTextPassword.text.toString()
        val name = editTextName.text.toString()
        val address = editTextAddress.text.toString()
        val phone = editTextPhone.text.toString()
        val dateOfBirth = editTextDateOfBirth.text.toString()
        val gender = spinnerGender.selectedItem.toString()
        val bloodType = spinnerBloodType.selectedItem.toString()

        userViewModel.createUser(
            userId = userId,
            password = password,
            name = name,
            address = address,
            dateOfBirth = dateOfBirth,
            gender = gender,
            bloodType = bloodType,
            phone = phone
        )

        userViewModel.getUser(userId)
    }

}