package com.example.cronometrocorreto

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Menu : AppCompatActivity() {

    val SHARED: String = "sharedPrefs"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)


        var sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)


        val buttonC = findViewById<Button>(R.id.button_chronometer)
        val buttonT = findViewById<Button>(R.id.button_timer)
        var name: EditText = findViewById(R.id.name_field)
        var nameStr : String

        buttonC.setOnClickListener {
            val editor = sharedPreferences.edit()
            nameStr = name.text.toString()

            editor.putString("name", nameStr)
            editor.commit()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonT.setOnClickListener {
            val editor = sharedPreferences.edit()
            nameStr = name.text.toString()

            editor.putString("name", nameStr)
            editor.commit()

            val intent = Intent(this, Temporizado::class.java)
            startActivity(intent)
        }
    }
}