package com.example.leptinitracker.model

import androidx.room.*
import java.util.*

// Phase of the Leptini Method (e.g., Hydration, Vegetable Introduction)
@Entity(tableName = "phases")
data class Phase(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val startWeek: Int,
    val endWeek: Int,
    val allowedFoods: String,
    val restrictedFoods: String
)

// Task to complete in a specific phase (e.g., Drink water, Add vegetables)
@Entity(tableName = "tasks", foreignKeys = [
    ForeignKey(
        entity = Phase::class,
        parentColumns = ["id"],
        childColumns = ["phaseId"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Task(
    @PrimaryKey val id: String,
    val phaseId: String,
    val name: String,
    val description: String
)

// User's completed task record
@Entity(tableName = "completed_tasks", primaryKeys = ["taskId", "date"])
data class CompletedTask(
    val taskId: String,
    val date: String, // Format: YYYY-MM-DD
    val completed: Boolean = true
)

// Weight record
@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weight: Float,
    val date: Long, // Timestamp
    val notes: String? = null
)

// Water intake record
@Entity(tableName = "water_intake")
data class WaterIntake(
    @PrimaryKey val date: String, // Format: YYYY-MM-DD
    val cups: Int
)

// User settings and progress
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Single user profile
    val startDate: Long, // When the program was started
    val startWeight: Float? = null,
    val targetWeight: Float? = null,
    val currentWeek: Int = 1,
    val currentDay: Int = 1,
    val selectedPath: String = "cleanse", // For weeks 9+: fast, cleanse, moderate
    val eatingWindowStart: Int = 8, // Hour (24-hour format)
    val eatingWindowEnd: Int = 20 // Hour (24-hour format)
)

// Meal suggestion
@Entity(tableName = "meal_suggestions")
data class MealSuggestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phaseId: String,
    val path: String? = null, // null means applies to all paths
    val mealType: String, // breakfast, lunch, dinner
    val name: String,
    val description: String,
    val ingredients: String
)

// Relationship classes to retrieve data more easily
data class PhaseWithTasks(
    @Embedded val phase: Phase,
    @Relation(
        parentColumn = "id",
        entityColumn = "phaseId"
    )
    val tasks: List<Task>
)

// Constants for the different Leptini phases
object LeptiniPhases {
    const val HYDRATION = "hydration"
    const val VEGETABLES = "vegetables"
    const val CLEANSE_START = "cleanseStart"
    const val CARB_OPTIMIZATION = "carbOptimization"
    const val FAT_MANAGEMENT = "fatManagement"
    const val MEAL_TIMING = "mealTiming"
    const val RELEASE = "release"
}

// Constants for the Leptini paths in the release phase
object LeptiniPaths {
    const val FAST = "fast"
    const val CLEANSE = "cleanse"
    const val MODERATE = "moderate"
}