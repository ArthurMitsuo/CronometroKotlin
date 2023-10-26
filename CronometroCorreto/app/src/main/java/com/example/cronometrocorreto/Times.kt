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

    //Pega a instância do banco de dados na URL do firebase
    private val database = Firebase.database("https://cronometro-c0889-default-rtdb.firebaseio.com/");
    //Pega a referência de users dentro do banco de dados
    private var myRef = database.getReference("users");


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_times)

        //Cria uma variável que pode receber qualquer tipo de dados, para salvar o que for recebido do Get
        var snapshotSave : Any

        returnButton = findViewById(R.id.return_button);
        eraseButton = findViewById(R.id.erase_button);
        times = findViewById(R.id.times);

        //Método get
        myRef.get().addOnSuccessListener {dataSnapshot ->
            Toast.makeText(this, R.string.succeded_download, Toast.LENGTH_SHORT).show()

            //Verifica se algo foi recebido na variavel dataSnapshot
            if(dataSnapshot.exists()){
                var mensagem:String = "";

                //Verifica cada item nos filhos do objeto recebido
                for (childSnapshot in dataSnapshot.children) {
                    //Salva a chave, nome de usuario, como chave
                    val chave = childSnapshot.key

                    var timesVar : String = "";
                    //verifica dentro dos filhos do childSnapshot, os tempos, cada um deles
                    for(childTimes in childSnapshot.children){
                        val timesAux = childTimes.value

                        //salva os tempos como toString()
                        val t = timesAux.toString()
                        //a formatação abaixo não funcionou como esperado, os tempos vem em uma array, ainda não soube como iterar nela.
                        val previousTimeLog = timesVar.toString()
                        timesVar = "$previousTimeLog$t"
                    }


                    val user = chave.toString()
                    val previousTimeLog = mensagem.toString()
                    //salva em uma variável mensagem, conforme for iterando nos filhos do dataSnapshot
                    mensagem = "$previousTimeLog\n$user:\n $timesVar"
                }

                times.text = mensagem;
            }else {
                //Caso não tenha nada no método GET, mostra a variável string nothing_found
                times.setText(R.string.nothing_found);
            }
        }.addOnFailureListener{
            //Caso de erro no get, mostra um Toast
            Toast.makeText(this, R.string.failed_download, Toast.LENGTH_SHORT).show()
        }


        returnButton.setOnClickListener{
            //Mostra um Toast e retorna para a Activity Anterior
            Toast.makeText(this, R.string.returning, Toast.LENGTH_SHORT).show();
            finish();
        }

        eraseButton.setOnClickListener{
            //Método HTTP para remoção de valor
            myRef.removeValue().addOnSuccessListener {
                //Dando certo, mostra um TOAST e volta para a activity anterior. Mesmo vazio o banco, funciona.
                Toast.makeText(this, R.string.succeded_erase, Toast.LENGTH_SHORT).show()
                finish();
            }.addOnFailureListener{
                //Dando errado, mostra um TOAST de falha apenas.
                Toast.makeText(this, R.string.failed_erase, Toast.LENGTH_SHORT).show()
            }
        }
    }
}