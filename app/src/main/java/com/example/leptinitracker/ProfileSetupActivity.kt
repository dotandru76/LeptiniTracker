// Create a file called ProfileSetupActivity.kt
package com.example.leptinitracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var startWeightEditText: EditText
    private lateinit var targetWeightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        startWeightEditText = findViewById(R.id.startWeightEditText)
        targetWeightEditText = findViewById(R.id.targetWeightEditText)
        heightEditText = findViewById(R.id.heightEditText)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        saveButton = findViewById(R.id.saveButton)

        // Set up save button click listener
        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveProfileData()
                navigateToMainActivity()
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (nameEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val age = ageEditText.text.toString().toInt()
            if (age <= 0 || age > 120) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val startWeight = startWeightEditText.text.toString().toFloat()
            if (startWeight <= 0 || startWeight > 500) {
                Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val targetWeight = targetWeightEditText.text.toString().toFloat()
            if (targetWeight <= 0 || targetWeight > 500) {
                Toast.makeText(this, "Please enter a valid target weight", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid target weight", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val height = heightEditText.text.toString().toInt()
            if (height <= 0 || height > 300) {
                Toast.makeText(this, "Please enter a valid height", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid height", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveProfileData() {
        val sharedPref = getSharedPreferences("LeptiniUserProfile", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("name", nameEditText.text.toString())
            putInt("age", ageEditText.text.toString().toInt())
            putFloat("startWeight", startWeightEditText.text.toString().toFloat())
            putFloat("targetWeight", targetWeightEditText.text.toString().toFloat())
            putInt("height", heightEditText.text.toString().toInt())

            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val gender = if (selectedGenderId != -1) {
                val radioButton = findViewById<RadioButton>(selectedGenderId)
                radioButton.text.toString()
            } else {
                "Not specified"
            }
            putString("gender", gender)

            // Store program start date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            putString("startDate", dateFormat.format(Date()))

            // Set profile as completed
            putBoolean("profileCompleted", true)

            // Set starting week and day
            putInt("currentWeek", 1)
            putInt("currentDay", 1)

            apply()
        }

        // Also add the initial weight entry
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val weightEntries = sharedPref.getString("weightEntries", "") ?: ""
        val newEntry = "$today,${startWeightEditText.text.toString()}"
        val updatedEntries = if (weightEntries.isEmpty()) newEntry else "$weightEntries|$newEntry"

        with(sharedPref.edit()) {
            putString("weightEntries", updatedEntries)
            apply()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close this activity so user can't go back
    }
}