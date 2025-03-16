package com.example.leptinitracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.leptinitracker.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.*

@Database(
    entities = [
        Phase::class,
        Task::class,
        CompletedTask::class,
        WeightEntry::class,
        WaterIntake::class,
        UserProfile::class,
        MealSuggestion::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LeptiniDatabase : RoomDatabase() {

    abstract fun phaseDao(): PhaseDao
    abstract fun taskDao(): TaskDao
    abstract fun completedTaskDao(): CompletedTaskDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun mealSuggestionDao(): MealSuggestionDao

    companion object {
        @Volatile
        private var INSTANCE: LeptiniDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LeptiniDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LeptiniDatabase::class.java,
                    "leptini_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(LeptiniDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class LeptiniDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        prepopulateDatabase(database)
                    }
                }
            }
        }

        // Pre-populate the database with Leptini Method data
        private suspend fun prepopulateDatabase(database: LeptiniDatabase) {
            // Add phases
            val phaseDao = database.phaseDao()

            val hydrationPhase = Phase(
                id = LeptiniPhases.HYDRATION,
                name = "Hydration Focus",
                description = "Focus on drinking water before meals to reduce false hunger signals.",
                startWeek = 1,
                endWeek = 1,
                allowedFoods = "All foods are allowed during this phase. The focus is on proper hydration.",
                restrictedFoods = "No restrictions yet. Just focus on water intake."
            )

            val vegetablesPhase = Phase(
                id = LeptiniPhases.VEGETABLES,
                name = "Vegetable Introduction",
                description = "Add cleaning vegetables to fill 50% of your plate at each meal.",
                startWeek = 2,
                endWeek = 2,
                allowedFoods = "All foods, with emphasis on increasing vegetable intake to 50% of each plate.",
                restrictedFoods = "No specific restrictions, just focus on the 50% vegetable rule."
            )

            val cleanseStartPhase = Phase(
                id = LeptiniPhases.CLEANSE_START,
                name = "Leptini Cleanse Begins",
                description = "Begin planned breaks from foods that cause hormonal blockage.",
                startWeek = 3,
                endWeek = 4,
                allowedFoods = "Vegetables, proteins, whole foods, regular carbs (still allowed).",
                restrictedFoods = "Sugar, flour, processed foods - these cause blockage effect."
            )

            val carbOptimizationPhase = Phase(
                id = LeptiniPhases.CARB_OPTIMIZATION,
                name = "Carbohydrate Optimization",
                description = "Switch to leptin-friendly carbohydrates with higher fiber content.",
                startWeek = 5,
                endWeek = 6,
                allowedFoods = "Vegetables, proteins, leptin-friendly carbs (legumes, quinoa, buckwheat), berries.",
                restrictedFoods = "Sugar, flour, processed foods, rice, potatoes, regular grains, limit non-berry fruits to 1/day."
            )

            val fatManagementPhase = Phase(
                id = LeptiniPhases.FAT_MANAGEMENT,
                name = "Fat Management",
                description = "Limit concentrated fats while maintaining satiety.",
                startWeek = 7,
                endWeek = 7,
                allowedFoods = "Vegetables, proteins, leptin-friendly carbs, berries, limited measured fats.",
                restrictedFoods = "Sugar, flour, processed foods, rice, potatoes, regular grains, nuts, seeds, fried foods, limit fats to 2 tbsp/day."
            )

            val mealTimingPhase = Phase(
                id = LeptiniPhases.MEAL_TIMING,
                name = "Meal Timing",
                description = "Establish eating windows and reduce meal frequency.",
                startWeek = 8,
                endWeek = 8,
                allowedFoods = "Same as fat management phase, with focus on fewer, more substantial meals.",
                restrictedFoods = "Same as fat management phase plus snacking outside established eating window."
            )

            val releasePhase = Phase(
                id = LeptiniPhases.RELEASE,
                name = "Leptini Release",
                description = "Choose your maintenance path for sustainable results.",
                startWeek = 9,
                endWeek = 13,
                allowedFoods = "Depends on chosen path (Fast, Cleanse, or Moderate)",
                restrictedFoods = "Depends on chosen path, but processed foods remain limited"
            )

            phaseDao.insertAll(
                hydrationPhase,
                vegetablesPhase,
                cleanseStartPhase,
                carbOptimizationPhase,
                fatManagementPhase,
                mealTimingPhase,
                releasePhase
            )

            // Add tasks for each phase
            val taskDao = database.taskDao()

            // Hydration phase tasks
            taskDao.insertAll(
                Task(
                    id = "water_hydration",
                    phaseId = LeptiniPhases.HYDRATION,
                    name = "Drink 3-4 liters of water daily",
                    description = "2 cups before each meal"
                ),
                Task(
                    id = "hunger_hydration",
                    phaseId = LeptiniPhases.HYDRATION,
                    name = "Notice hunger signals",
                    description = "Pay attention to true hunger vs. mouth hunger"
                )
            )

            // Vegetables phase tasks
            taskDao.insertAll(
                Task(
                    id = "water_vegetables",
                    phaseId = LeptiniPhases.VEGETABLES,
                    name = "Drink 3-4 liters of water daily",
                    description = "2 cups before each meal"
                ),
                Task(
                    id = "veggies_vegetables",
                    phaseId = LeptiniPhases.VEGETABLES,
                    name = "Fill 50% of plate with vegetables",
                    description = "Preferably raw vegetables"
                ),
                Task(
                    id = "support_vegetables",
                    phaseId = LeptiniPhases.VEGETABLES,
                    name = "Find a support person",
                    description = "Someone to encourage your journey"
                )
            )

            // Cleanse Start phase tasks
            taskDao.insertAll(
                Task(
                    id = "water_cleanse",
                    phaseId = LeptiniPhases.CLEANSE_START,
                    name = "Drink 3-4 liters of water daily",
                    description = "2 cups before each meal"
                ),
                Task(
                    id = "veggies_cleanse",
                    phaseId = LeptiniPhases.CLEANSE_START,
                    name = "Fill 50% of plate with vegetables",
                    description = "Preferably raw vegetables"
                ),
                Task(
                    id = "protein_cleanse",
                    phaseId = LeptiniPhases.CLEANSE_START,
                    name = "Include protein with every meal",
                    description = "Plant or animal based protein"
                ),
                Task(
                    id = "sugar_cleanse",
                    phaseId = LeptiniPhases.CLEANSE_START,
                    name = "Avoid sugar and flour",
                    description = "Planned break from processed foods"
                )
            )

            // Create initial user profile
            val userProfileDao = database.userProfileDao()
            userProfileDao.insert(
                UserProfile(
                    startDate = System.currentTimeMillis(),
                    currentWeek = 1,
                    currentDay = 1
                )
            )
        }
    }
}

// Type converters for Room
class Converters {
    @androidx.room.TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @androidx.room.TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}