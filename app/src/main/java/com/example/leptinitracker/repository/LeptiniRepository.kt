package com.example.leptinitracker.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.leptinitracker.database.*
import com.example.leptinitracker.model.*
import java.text.SimpleDateFormat
import java.util.*

class LeptiniRepository(
    private val phaseDao: PhaseDao,
    private val taskDao: TaskDao,
    private val completedTaskDao: CompletedTaskDao,
    private val weightEntryDao: WeightEntryDao,
    private val waterIntakeDao: WaterIntakeDao,
    private val userProfileDao: UserProfileDao,
    private val mealSuggestionDao: MealSuggestionDao
) {
    // User profile operations
    val userProfile: LiveData<UserProfile> = userProfileDao.getUserProfile()

    @WorkerThread
    suspend fun updateCurrentProgress(week: Int, day: Int) {
        userProfileDao.updateCurrentDay(week, day)
    }

    @WorkerThread
    suspend fun advanceDay(currentWeek: Int, currentDay: Int) {
        if (currentDay < 7) {
            userProfileDao.updateCurrentDay(currentWeek, currentDay + 1)
        } else if (currentWeek < 13) {
            userProfileDao.updateCurrentDay(currentWeek + 1, 1)
        }
    }

    @WorkerThread
    suspend fun updateSelectedPath(path: String) {
        userProfileDao.updateSelectedPath(path)
    }

    @WorkerThread
    suspend fun updateWeightGoals(startWeight: Float?, targetWeight: Float?) {
        startWeight?.let { userProfileDao.updateStartWeight(it) }
        targetWeight?.let { userProfileDao.updateTargetWeight(it) }
    }

    @WorkerThread
    suspend fun updateEatingWindow(startHour: Int, endHour: Int) {
        userProfileDao.updateEatingWindow(startHour, endHour)
    }

    // Phase and task operations
    fun getPhaseForWeek(week: Int): LiveData<Phase> = phaseDao.getPhaseForWeek(week)

    fun getPhaseWithTasksForWeek(week: Int): LiveData<PhaseWithTasks> =
        phaseDao.getPhaseWithTasksForWeek(week)

    fun getPhaseById(phaseId: String): LiveData<Phase> = phaseDao.getPhaseById(phaseId)

    fun getTasksForPhase(phaseId: String): LiveData<List<Task>> = taskDao.getTasksForPhase(phaseId)

    // Completed tasks operations
    fun getCompletedTasksForToday(): LiveData<List<CompletedTask>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        return completedTaskDao.getCompletedTasksForDate(today)
    }

    fun getCompletedTasksForDate(date: Date): LiveData<List<CompletedTask>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        return completedTaskDao.getCompletedTasksForDate(formattedDate)
    }

    fun isTaskCompleted(taskId: String, date: Date): LiveData<CompletedTask?> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        return completedTaskDao.getCompletedTask(taskId, formattedDate)
    }

    @WorkerThread
    suspend fun completeTask(taskId: String, date: Date = Date()) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        completedTaskDao.insert(CompletedTask(taskId, formattedDate))
    }

    @WorkerThread
    suspend fun uncompleteTask(taskId: String, date: Date = Date()) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        completedTaskDao.deleteCompletedTask(taskId, formattedDate)
    }

    // Weight tracking operations
    val allWeightEntries: LiveData<List<WeightEntry>> = weightEntryDao.getAllWeightEntries()

    val latestWeight: LiveData<WeightEntry?> = weightEntryDao.getLatestWeightEntry()

    val firstWeight: LiveData<WeightEntry?> = weightEntryDao.getFirstWeightEntry()

    fun getWeightEntriesInRange(startDate: Long, endDate: Long): LiveData<List<WeightEntry>> =
        weightEntryDao.getWeightEntriesInRange(startDate, endDate)

    @WorkerThread
    suspend fun addWeightEntry(weight: Float, date: Date = Date(), notes: String? = null): Long {
        val entry = WeightEntry(
            weight = weight,
            date = date.time,
            notes = notes
        )
        return weightEntryDao.insert(entry)
    }

    @WorkerThread
    suspend fun deleteWeightEntry(id: Int) {
        weightEntryDao.deleteById(id)
    }

    // Water intake operations
    fun getWaterIntakeForToday(): LiveData<WaterIntake?> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        return waterIntakeDao.getWaterIntakeForDate(today)
    }

    fun getWaterIntakeForDate(date: Date): LiveData<WaterIntake?> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        return waterIntakeDao.getWaterIntakeForDate(formattedDate)
    }

    @WorkerThread
    suspend fun updateWaterIntake(cups: Int, date: Date = Date()) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        waterIntakeDao.insert(WaterIntake(formattedDate, cups))
    }

    // Meal suggestion operations
    fun getMealSuggestionsForCurrentPhase(week: Int, path: String? = null): LiveData<List<MealSuggestion>> {
        // First get the phase ID based on the week
        val phaseId = when (week) {
            1 -> LeptiniPhases.HYDRATION
            2 -> LeptiniPhases.VEGETABLES
            3, 4 -> LeptiniPhases.CLEANSE_START
            5, 6 -> LeptiniPhases.CARB_OPTIMIZATION
            7 -> LeptiniPhases.FAT_MANAGEMENT
            8 -> LeptiniPhases.MEAL_TIMING
            else -> LeptiniPhases.RELEASE
        }

        // Then get meal suggestions, with optional path filter for release phase
        val pathFilter = if (week >= 9) path else null
        return mealSuggestionDao.getMealSuggestionsForPhase(phaseId, pathFilter)
    }
}