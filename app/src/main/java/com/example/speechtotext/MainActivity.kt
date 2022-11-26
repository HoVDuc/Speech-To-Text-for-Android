package com.example.speechtotext

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button_send)

        button.setOnClickListener {
            val textFileNameEdit = findViewById<EditText>(R.id.filename)
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("fileName", textFileNameEdit.text.toString())
            startActivity(intent)
        }
    }
}