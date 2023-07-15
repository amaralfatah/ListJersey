package com.dicoding.todoapp.ui.detail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.databinding.ActivityAddTaskBinding
import com.dicoding.todoapp.databinding.ActivityTaskDetailBinding
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.list.TaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID
import com.squareup.picasso.Picasso

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailBinding
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.title = "Detail Pesanan"


        //TODO 11 : Show detail task and implement delete action
        val idTask = intent.getIntExtra(TASK_ID,1)
        val factory = ViewModelFactory.getInstance(this)
        val model = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        // Menginisialisasi AutoCompleteTextView dan mengatur teks hint
        val autoCompleteTextViewBahan: AutoCompleteTextView =
            findViewById(R.id.autoCompleteTextViewBahan)

        // Mendapatkan daftar provinsi dari resources
        val bahanSpinner = resources.getStringArray(R.array.bahanSpinner)

        // Inisialisasi adapter untuk AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bahanSpinner)
        autoCompleteTextViewBahan.setAdapter(adapter)


        model.setTaskId(idTask)
        model.task.observe(this){
            binding.edNama.setText(it.namaPelanggan)
            binding.edAlamat.setText(it.alamat)
//          binding.ivPreview.setImageURI(Uri.parse(it.imagePath))
            Picasso.get().load(Uri.parse(it.imagePath)).into(binding.ivPreview)
//          Menampilkan gambar yang terpilih
            binding.ivPreview.visibility = View.VISIBLE
            binding.autoCompleteTextViewBahan.text.toString()
            binding.edJumlah.setText(it.jumlah.toString())
            binding.dueDate.text = DateConverter.convertMillisToString(it.dueDateMillis)
            binding.edNote.setText(it.note)

            binding.btnHapus.setOnClickListener {
                model.task.removeObservers(this)
                model.deleteTask()

                val toDetail = Intent(this, TaskActivity::class.java)
                this.startActivity(toDetail)
            }
        }
    }
}