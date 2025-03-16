package com.example.leptinitracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.leptinitracker.model.*
import java.util.*

@Dao
interface PhaseDao {
    @Query("SELECT * FROM phases ORDER BY startWeek")
    fun getAllPhases(): LiveData<List<Phase>>

    @Query("SELECT * FROM phases WHERE id = :phaseId")
    fun getPhaseById(phaseId: String): LiveData<Phase>

    @Query("SELECT * FROM phases WHERE :week BETWEEN startWeek AND endWeek LIMIT 1")
    fun getPhaseForWeek(week: Int): LiveData<Phase>

    @Transaction
    @Query("SELECT * FROM phases WHERE id = :phaseId")
    fun getPhaseWithTasks(phaseId: String): LiveData<PhaseWithTasks>

    @Transaction
    @Query("SELECT * FROM phases WHERE :week BETWEEN startWeek AND endWeek LIMIT 1")
    fun getPhaseWithTasksForWeek(week: Int): LiveData<PhaseWithTasks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(phase: Phase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg phases: Phase)

    @Update
    suspend fun update(phase: Phase)

    @Delete
    suspend fun delete(phase: Phase)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE phaseId = :phaseId")
    fun getTasksForPhase(phaseId: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: String): LiveData<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tasks: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}

@Dao
interface CompletedTaskDao {
    @Query("SELECT * FROM completed_tasks WHERE date = :date")
    fun getCompletedTasksForDate(date: String): LiveData<List<CompletedTask>>

    @Query("SELECT * FROM completed_tasks WHERE taskId = :taskId AND date = :date")
    fun getCompletedTask(taskId: String, date: String): LiveData<CompletedTask?>

    @Query("SELECT COUNT(*) FROM completed_tasks WHERE date = :date")
    fun getCompletedTasksCountForDate(date: String): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completedTask: CompletedTask)

    @Delete
    suspend fun delete(completedTask: CompletedTask)

    @Query("DELETE FROM completed_tasks WHERE taskId = :taskId AND date = :date")
    suspend fun deleteCompletedTask(taskId: String, date: String)
}

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAllWeightEntries(): LiveData<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT 1")
    fun getLatestWeightEntry(): LiveData<WeightEntry?>

    @Query("SELECT * FROM weight_entries ORDER BY date ASC LIMIT 1")
    fun getFirstWeightEntry(): LiveData<WeightEntry?>

    @Query("SELECT * FROM weight_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    fun getWeightEntriesInRange(startDate: Long, endDate: Long): LiveData<List<WeightEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weightEntry: WeightEntry): Long

    @Update
    suspend fun update(weightEntry: WeightEntry)

    @Delete
    suspend fun delete(weightEntry: WeightEntry)

    @Query("DELETE FROM weight_entries WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface WaterIntakeDao {
    @Query("SELECT * FROM water_intake WHERE date = :date")
    fun getWaterIntakeForDate(date: String): LiveData<WaterIntake?>

    @Query("SELECT * FROM water_intake ORDER BY date DESC")
    fun getAllWaterIntake(): LiveData<List<WaterIntake>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(waterIntake: WaterIntake)

    @Update
    suspend fun update(waterIntake: WaterIntake)

    @Delete
    suspend fun delete(waterIntake: WaterIntake)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): LiveData<UserProfile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile)

    @Update
    suspend fun update(userProfile: UserProfile)

    @Query("UPDATE user_profile SET currentWeek = :week, currentDay = :day WHERE id = 1")
    suspend fun updateCurrentDay(week: Int, day: Int)

    @Query("UPDATE user_profile SET selectedPath = :path WHERE id = 1")
    suspend fun updateSelectedPath(path: String)

    @Query("UPDATE user_profile SET startWeight = :weight WHERE id = 1")
    suspend fun updateStartWeight(weight: Float)

    @Query("UPDATE user_profile SET targetWeight = :weight WHERE id = 1")
    suspend fun updateTargetWeight(weight: Float)

    @Query("UPDATE user_profile SET eatingWindowStart = :startHour, eatingWindowEnd = :endHour WHERE id = 1")
    suspend fun updateEatingWindow(startHour: Int, endHour: Int)
}

@Dao
interface MealSuggestionDao {
    @Query("SELECT * FROM meal_suggestions WHERE phaseId = :phaseId AND (path IS NULL OR path = :path) ORDER BY mealType")
    fun getMealSuggestionsForPhase(phaseId: String, path: String? = null): LiveData<List<MealSuggestion>>

    @Query("SELECT * FROM meal_suggestions WHERE phaseId = :phaseId AND mealType = :mealType AND (path IS NULL OR path = :path)")
    fun getMealSuggestionsByType(phaseId: String, mealType: String, path: String? = null): LiveData<List<MealSuggestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mealSuggestion: MealSuggestion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg mealSuggestions: MealSuggestion)

    @Update
    suspend fun update(mealSuggestion: MealSuggestion)

    @Delete
    suspend fun delete(mealSuggestion: MealSuggestion)
}