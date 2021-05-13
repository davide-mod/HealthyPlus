package com.modolo.healthyplus.mealplanner.mealdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*classe per il database locale di pasti*/

@Database(entities = [Meal::class], version = 1, exportSchema = false)

abstract class MealDatabase : RoomDatabase() {

    abstract fun mealDAO(): MealDAO

    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getDatabase(context: Context): MealDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MealDatabase::class.java,
                        "meals.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}