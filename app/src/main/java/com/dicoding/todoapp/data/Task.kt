package com.dicoding.todoapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO 1 : Define a local database table using the schema in app/schema/tasks.json

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    val namaPelanggan: String,

    val alamat: String,

    val noHp: String,

    val imagePath: String,

    val bahan: String,

    val model: String,

    val jumlah: Int,

    @ColumnInfo(name = "dueDateMillis")
    val dueDateMillis: Long,

    val note: String,

    @ColumnInfo(name = "completed")
    val isCompleted: Boolean = false
)
