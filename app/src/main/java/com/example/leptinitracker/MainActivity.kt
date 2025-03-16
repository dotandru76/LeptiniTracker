package com.example.leptinitracker
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.leptinitracker.databinding.ActivityMainBinding
import com.example.leptinitracker.model.CompletedTask
import com.example.leptinitracker.model.Phase
import com.example.leptinitracker.model.PhaseWithTasks
import com.example.leptinitracker.viewmodel.LeptiniViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: LeptiniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if profile is completed
        val sharedPref = getSharedPreferences("LeptiniUserProfile", Context.MODE_PRIVATE)
        val profileCompleted = sharedPref.getBoolean("profileCompleted", false)

        if (!profileCompleted) {
            // If profile is not completed, go to profile setup
            val intent = Intent(this, ProfileSetupActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Continue with normal initialization
        setContentView(R.layout.activity_main)

        // Rest of your existing code...
    }

    private fun setupObservers() {
        // Observe user profile changes
        viewModel.userProfile.observe(this) { profile ->
            binding.textWeekDay.text = "Week ${profile.currentWeek}, Day ${profile.currentDay}"
        }

        // Observe current phase
        viewModel.getCurrentPhase().observe(this) { phase ->
            binding.textPhaseName.text = phase.name
            binding.textPhaseDescription.text = phase.description
        }

        // Observe messages from ViewModel
        viewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Observe water intake
        viewModel.currentWaterIntake.observe(this) { waterIntake ->
            val cups = waterIntake?.cups ?: 0
            binding.textWaterCount.text = "$cups cups"
        }
    }

    private fun setupListeners() {
        // Complete day button
        binding.buttonAdvanceDay.setOnClickListener {
            viewModel.advanceDay()
        }

        // Water controls
        binding.buttonWaterIncrement.setOnClickListener {
            viewModel.incrementWater()
        }

        binding.buttonWaterDecrement.setOnClickListener {
            viewModel.decrementWater()
        }
    }
}