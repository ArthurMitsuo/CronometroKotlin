package com.example.cronometrocorreto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Times : AppCompatActivity() {
    private lateinit var returnButton: Button
    private lateinit var eraseButton: Button
    private lateinit var times: TextView

    private val database = Firebase.database("https://cronometro-c0889-default-rtdb.firebaseio.com/");
    private var myRef = database.getReference("users");


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_times)

        var snapshotSave : Any

        returnButton = findViewById(R.id.return_button);
        eraseButton = findViewById(R.id.erase_button);
        times = findViewById(R.id.times);

        myRef.get().addOnSuccessListener {dataSnapshot ->
            Toast.makeText(this, R.string.succeded_download, Toast.LENGTH_SHORT).show()

            if(dataSnapshot.exists()){
                snapshotSave = dataSnapshot;

                var mensagem:String = "";

                for (childSnapshot in dataSnapshot.children) {
                    val chave = childSnapshot.key
                    var timesVar : String = "";
                    //val valor = childSnapshot.value
                    for(childTimes in childSnapshot.children){
                        val timesAux = childTimes.value

                        val t = timesAux.toString()
                        val previousTimeLog = timesVar.toString()
                        timesVar = "$previousTimeLog$t"
                    }
                    //println("Chave: $chave, Valor: $valor")

                    val user = chave.toString()
                    val previousTimeLog = mensagem.toString()
                    mensagem = "$previousTimeLog\n$user:\n $timesVar"
                }
                Log.d("username", dataSnapshot.children.toString());
                times.text = mensagem;
            }else {
                times.setText(R.string.nothing_found);
            }
        }.addOnFailureListener{
            Toast.makeText(this, R.string.failed_download, Toast.LENGTH_SHORT).show()
        }


        returnButton.setOnClickListener{
            Toast.makeText(this, R.string.returning, Toast.LENGTH_SHORT).show();
            finish();
        }

        eraseButton.setOnClickListener{
            myRef.removeValue().addOnSuccessListener {dataSnapshot ->
                Toast.makeText(this, R.string.succeded_erase, Toast.LENGTH_SHORT).show()
                finish();
            }.addOnFailureListener{
                Toast.makeText(this, R.string.failed_erase, Toast.LENGTH_SHORT).show()
            }
        }
    }
}