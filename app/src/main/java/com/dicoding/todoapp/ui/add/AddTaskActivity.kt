package com.dicoding.todoapp.ui.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.databinding.ActivityAddTaskBinding
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.list.TaskActivity
import com.dicoding.todoapp.utils.DatePickerFragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener {
    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }

    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    private var dueDateMillis: Long = System.currentTimeMillis()
    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.title = "Tambah Pesanan"

        // Menginisialisasi AutoCompleteTextView dan mengatur teks hint
        val autoCompleteTextViewBahan: AutoCompleteTextView =
            findViewById(R.id.autoCompleteTextViewBahan)

        // Mendapatkan daftar provinsi dari resources
        val bahanSpinner = resources.getStringArray(R.array.bahanSpinner)

        // Inisialisasi adapter untuk AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bahanSpinner)
        autoCompleteTextViewBahan.setAdapter(adapter)

        //menu bahan langsung tampil
        autoCompleteTextViewBahan.setOnClickListener {
            autoCompleteTextViewBahan.showDropDown()
        }

        val spinnerModeJahit: Spinner = findViewById(R.id.spinnerModeJahit)
        val modeJahitOptions = resources.getStringArray(R.array.modeJahit)

        // Inisialisasi adapter untuk Spinner
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, modeJahitOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModeJahit.adapter = adapterSpinner

        binding.chooseImageButton.setOnClickListener {
            checkStoragePermission()
        }

        binding.btnSave.setOnClickListener {
            save()
        }
    }

    private fun save() {
        if (imageUri.toString().isEmpty()) {
            Toast.makeText(this, "Foto tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else if (!resources.getStringArray(R.array.bahanSpinner)
                .contains(binding.autoCompleteTextViewBahan.text.toString())
        ) {
            Toast.makeText(this, "Bahan tidak boleh yang tidak ada dalam list", Toast.LENGTH_SHORT)
                .show()
        } else if (
            binding.edNama.text.toString().isEmpty() ||
            binding.edAlamat.text.toString().isEmpty() ||
            binding.edNoHp.text.toString().isEmpty() ||
            imageUri.toString().isEmpty() ||
            binding.autoCompleteTextViewBahan.text.toString().isEmpty() ||
            binding.edJumlah.text.toString().isEmpty() ||
            binding.edNote.text.toString().isEmpty()
        ) {
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT)
                .show()
        } else {
            val factory = ViewModelFactory.getInstance(this)
            val model = ViewModelProvider(this, factory)[AddTaskViewModel::class.java]
            model.addTask(
                Task(
                    0,
                    binding.edNama.text.toString(),
                    binding.edAlamat.text.toString(),
                    binding.edNoHp.text.toString(),
                    imageUri.toString(),
                    binding.autoCompleteTextViewBahan.text.toString(),
                    binding.edJumlah.text.toString().toInt(),
                    dueDateMillis,
                    binding.edNote.text.toString()
                )
            )
            val detailIntent = Intent(this, TaskActivity::class.java)
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(detailIntent)
            finish()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
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
        findViewById<TextView>(R.id.dueDate).text = dateFormat.format(calendar.time)

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
                    Picasso.get().load(imageUri).into(binding.selectedImage)

                    imageBitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver, imageUri
                    )

                    binding.selectedImage.visibility = View.VISIBLE
                }

            }
        }
    }
}