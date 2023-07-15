package com.dicoding.todoapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import kotlinx.coroutines.launch

class DetailTaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _taskId = MutableLiveData<Int>()

    private val _task = _taskId.switchMap { id ->
        taskRepository.getTaskById(id)
    }
    val task: LiveData<Task> = _task

    fun setTaskId(taskId: Int?) {
        if (taskId == _taskId.value) {
            return
        }
        _taskId.value = taskId!!
    }

    fun deleteTask() {
        viewModelScope.launch {
            _task.value?.let { taskRepository.deleteTask(it) }
        }
    }

    fun updateTask(
        nama: String,
        alamat: String,
        noHp: String,
        imagePath: String,
        bahan: String,
        jumlah: Int,
        dueDateMillis: Long,
        note: String
    ) {
        viewModelScope.launch {
            _task.value?.let {
                taskRepository.updateTask(
                    it,
                    nama,
                    alamat,
                    noHp,
                    imagePath,
                    bahan,
                    jumlah,
                    dueDateMillis,
                    note
                )
            }
        }
    }
}