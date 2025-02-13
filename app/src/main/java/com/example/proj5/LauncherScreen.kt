package com.example.proj5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LauncherScreen: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_screen)
        val focusModeBtn = findViewById<Button>(R.id.focusModeBtn)
        val leisureModeBtn = findViewById<Button>(R.id.leisureModeBtn)


        focusModeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        leisureModeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }

    }
}