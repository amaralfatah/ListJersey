package com.dicoding.todoapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dicoding.todoapp.utils.FilterUtils
import com.dicoding.todoapp.utils.TasksFilterType

class TaskRepository(private val tasksDao: TaskDao) {

    companion object {
        const val PAGE_SIZE = 30
        const val PLACEHOLDERS = true

        @Volatile
        private var instance: TaskRepository? = null

        fun getInstance(context: Context): TaskRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    val database = TaskDatabase.getInstance(context)
                    instance = TaskRepository(database.taskDao())
                }
                return instance as TaskRepository
            }

        }
    }

    //TODO 4 : Use FilterUtils.getFilteredQuery to create filterable query
    //TODO 5 : Build PagedList with configuration
    fun getTasks(filter: TasksFilterType): LiveData<PagedList<Task>> {
        val query = FilterUtils.getFilteredQuery(filter)
        val tasks = tasksDao.getTasks(query)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(PLACEHOLDERS)
            .setInitialLoadSizeHint(30)
            .setPageSize(PAGE_SIZE)
            .build()
        return LivePagedListBuilder(tasks, config).build()
    }

    fun getTaskById(taskId: Int): LiveData<Task> {
        return tasksDao.getTaskById(taskId)
    }

    fun getNearestActiveTask(): Task {
        return tasksDao.getNearestActiveTask()
    }

    suspend fun insertTask(newTask: Task): Long {
        return tasksDao.insertTask(newTask)
    }

    suspend fun deleteTask(task: Task) {
        tasksDao.deleteTask(task)
    }

    suspend fun completeTask(task: Task, isCompleted: Boolean) {
        tasksDao.updateCompleted(task.id, isCompleted)
    }

    suspend fun updateTask(
        task: Task,
        nama: String,
        alamat: String,
        noHp: String,
        imagePath: String,
        bahan: String,
        model:String,
        jumlah: Int,
        dueDateMillis: Long,
        note: String
    ) {
        tasksDao.updateTask(
            task.id,
            nama,
            alamat,
            noHp,
            imagePath,
            bahan,
            model,
            jumlah,
            dueDateMillis,
            note
        )
    }

    fun getTaskFilter(filter: TasksFilterType = TasksFilterType.ALL_TASKS): LiveData<PagedList<Task>> {
        val query = FilterUtils.getFilteredQuery(filter)
        val tasks = tasksDao.getTasks(query)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(PLACEHOLDERS)
            .setInitialLoadSizeHint(30)
            .setPageSize(PAGE_SIZE)
            .build()
        return LivePagedListBuilder(tasks, config).build()
    }

    fun searchTask(query: String): LiveData<PagedList<Task>> {
        val tasks = tasksDao.searchTasks("%$query%")
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(PLACEHOLDERS)
            .setInitialLoadSizeHint(30)
            .setPageSize(PAGE_SIZE)
            .build()
        return LivePagedListBuilder(tasks, config).build()
    }
}