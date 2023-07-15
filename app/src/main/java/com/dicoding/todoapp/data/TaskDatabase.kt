package com.dicoding.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

//TODO 3 : Define room database class and prepopulate database using JSON
@Database(entities = [Task::class], version = 3, exportSchema = true)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                val preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val isLoading = preferences.getBoolean("isLoad", false)
                if (!isLoading) {
                    preferences.edit().putBoolean("isLoad", true).apply()
                    runBlocking {
                        withContext(Dispatchers.IO) {
                        }
                    }
                }
                instance
            }
        }
    }
}
