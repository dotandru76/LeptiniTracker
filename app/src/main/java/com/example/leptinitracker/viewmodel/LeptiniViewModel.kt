package com.example.leptinitracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.leptinitracker.database.LeptiniDatabase
import com.example.leptinitracker.model.*
import com.example.leptinitracker.repository.LeptiniRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class LeptiniViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LeptiniRepository
    val userProfile: LiveData<UserProfile>
    val allWeightEntries: LiveData<List<WeightEntry>>
    val latestWeight: LiveData<WeightEntry?>
    val currentWaterIntake: LiveData<WaterIntake?>

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    init {
        val database = LeptiniDatabase.getDatabase(application, viewModelScope)
        repository = LeptiniRepository(
            database.phaseDao(),
            database.taskDao(),
            database.completedTaskDao(),
            database.weightEntryDao(),
            database.waterIntakeDao(),
            database.userProfileDao(),
            database.mealSuggestionDao()
        )

        userProfile = repository.userProfile
        allWeightEntries = repository.allWeightEntries
        latestWeight = repository.latestWeight
        currentWaterIntake = repository.getWaterIntakeForToday()
    }

    fun getCurrentPhase(): LiveData<Phase> {
        val week = userProfile.value?.currentWeek ?: 1
        return repository.getPhaseForWeek(week)
    }

    fun getCurrentTasks(): LiveData<PhaseWithTasks> {
        val week = userProfile.value?.currentWeek ?: 1
        return repository.getPhaseWithTasksForWeek(week)
    }

    fun getCompletedTasks(): LiveData<List<CompletedTask>> {
        return repository.getCompletedTasksForToday()
    }

    fun advanceDay() {
        val profile = userProfile.value
        profile?.let {
            viewModelScope.launch(Dispatchers.IO) {
                repository.advanceDay(it.currentWeek, it.currentDay)
                _message.postValue("Advanced to Day ${if (it.currentDay < 7) it.currentDay + 1 else 1} of Week ${if (it.currentDay < 7) it.currentWeek else it.currentWeek + 1}")
            }
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.completeTask(taskId)
        }
    }

    fun uncompleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.uncompleteTask(taskId)
        }
    }

    fun addWeightEntry(weight: Float) {
        if (weight > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.addWeightEntry(weight)
                _message.postValue("Weight recorded: $weight kg")
            }
        } else {
            _message.value = "Please enter a valid weight"
        }
    }

    fun updateWaterIntake(cups: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWaterIntake(cups)
        }
    }

    fun incrementWater() {
        val current = currentWaterIntake.value?.cups ?: 0
        updateWaterIntake(current + 1)
    }

    fun decrementWater() {
        val current = currentWaterIntake.value?.cups ?: 0
        if (current > 0) {
            updateWaterIntake(current - 1)
        }
    }

    fun updateSelectedPath(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSelectedPath(path)
            _message.postValue("Updated to $path path")
        }
    }
}