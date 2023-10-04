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

        //Cria um SharedPreference de nome "sharedPrefs" com o contexto privado para a aplicação.
        var sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)


        val buttonC = findViewById<Button>(R.id.button_chronometer)
        val buttonT = findViewById<Button>(R.id.button_timer)
        var name: EditText = findViewById(R.id.name_field)
        //Cria o editor, que permite inserção de dados chave-valor no SharedPreference
        val editor = sharedPreferences.edit()
        var nameStr : String

        //ação que ouve o botão que leva ao Cronometro
        buttonC.setOnClickListener {
            //pega o nome inserido no input e manda com a chave "name" ao SharedPreference
            nameStr = name.text.toString()
            editor.putString("name", nameStr)
            editor.commit()

            //utiliza a classe intent para salvar a activity a ser utilizada e passa ao startActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonT.setOnClickListener {
            //pega o nome inserido no input e manda com a chave "name" ao SharedPreference
            nameStr = name.text.toString()
            editor.putString("name", nameStr)
            editor.commit()

            //utiliza a classe intent para salvar a activity a ser utilizada e passa ao startActivity
            val intent = Intent(this, Temporizado::class.java)
            startActivity(intent)
        }
    }
}