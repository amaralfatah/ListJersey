package com.dicoding.todoapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.todoapp.ui.list.TaskActivity

class SplashActivity : AppCompatActivity() {

    private val splashDuration = 2000L // Duration of the splash screen in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity to full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)

        // Set a handler to switch to the next page after the splash duration
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, TaskActivity::class.java)
            startActivity(intent)
            finish()
        }, splashDuration)
    }
}
