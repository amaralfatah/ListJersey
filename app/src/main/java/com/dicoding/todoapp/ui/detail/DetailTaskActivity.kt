package com.dicoding.todoapp.ui.detail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.databinding.ActivityTaskDetailBinding
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.list.TaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.DatePickerFragment
import com.dicoding.todoapp.utils.TASK_ID
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailTaskActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener {

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }

    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    private lateinit var binding: ActivityTaskDetailBinding
    private var dueDateMillis: Long = System.currentTimeMillis()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO 11 : Show detail task and implement delete action
        val idTask = intent.getIntExtra(TASK_ID, 1)
        val factory = ViewModelFactory.getInstance(this)
        val model = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]


        val bahanSpinner = resources.getStringArray(R.array.bahanSpinner)

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bahanSpinner)
        binding.autoCompleteTextViewBahan.setAdapter(adapter)

        model.setTaskId(idTask)
        model.task.observe(this) {
            binding.edNama.setText(it.namaPelanggan)
            binding.edAlamat.setText(it.alamat)
            binding.edNoHp.setText(it.noHp)
            binding.ivPreview.setImageURI(Uri.parse(it.imagePath))
            binding.ivPreview.isVisible = true
            imageUri = it.imagePath.toUri()
            binding.autoCompleteTextViewBahan.setText(it.bahan)
            binding.edJumlah.setText(it.jumlah.toString())
            binding.dueDate.text = DateConverter.convertMillisToString(it.dueDateMillis)
            binding.edNote.setText(it.note)

            binding.btnHapus.setOnClickListener {
                model.task.removeObservers(this)
                model.deleteTask()

                val toDetail = Intent(this, TaskActivity::class.java)
                this.startActivity(toDetail)
            }
            binding.btnSimpan.setOnClickListener {
                if (binding.edNama.text.toString().isEmpty() ||
                    binding.edAlamat.text.toString().isEmpty() ||
                    binding.edNoHp.text.toString().isEmpty() ||
                    imageUri.toString().isEmpty() ||
                    binding.autoCompleteTextViewBahan.text.toString().isEmpty() ||
                    binding.edJumlah.text.toString().isEmpty() ||
                    binding.edNote.text.toString().isEmpty()
                ) {
                    Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT)
                        .show()
                } else if (!resources.getStringArray(R.array.bahanSpinner)
                        .contains(binding.autoCompleteTextViewBahan.text.toString())
                ) {
                    Toast.makeText(
                        this,
                        "Bahan tidak boleh yang tidak ada dalam list",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    model.task.removeObservers(this)
                    model.updateTask(
                        binding.edNama.text.toString(),
                        binding.edAlamat.text.toString(),
                        binding.edNoHp.text.toString(),
                        imageUri.toString(),
                        binding.autoCompleteTextViewBahan.text.toString(),
                        binding.edJumlah.text.toString().toInt(),
                        dueDateMillis,
                        binding.edNote.text.toString()
                    )

                    val toDetail = Intent(this, TaskActivity::class.java)
                    this.startActivity(toDetail)
                }

            }
        }
        binding.chooseImageButton.setOnClickListener {
            checkStoragePermission()
        }
    }

    fun showDatePicker(view: View) {
        val dialogFragment = DatePickerFragment()
        dialogFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dueDate.text = dateFormat.format(calendar.time)

        dueDateMillis = calendar.timeInMillis
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pickImage()
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_IMAGE_PICKER
        )
    }

    private fun pickImage() {
        ImagePicker.with(this)
            .start(REQUEST_CODE_IMAGE_PICKER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_IMAGE_PICKER) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICKER -> {

                    imageUri = data!!.data as Uri
                    Picasso.get().load(imageUri).into(binding.ivPreview)

                    imageBitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver, imageUri
                    )
                }

            }
        }
    }
}