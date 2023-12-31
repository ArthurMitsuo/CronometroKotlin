package com.example.cronometrocorreto
//https://developer.android.com/training/data-storage/shared-preferences?hl=pt-br
import PersonModel
import android.content.Context
import android.content.Intent
import android.icu.util.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import org.w3c.dom.Text
import java.nio.charset.Charset
import java.util.Locale

class MainActivity<DataBasereference> : AppCompatActivity() {
    //criação das variáveis globais, privadas, para uso.
    private lateinit var timeTextView: TextView
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var noteButton: Button
    private lateinit var timeLog: TextView
    private lateinit var timeButton: Button
    private var isRunning = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private val handler = Handler()

    private var timesList = mutableListOf<String>()
    private lateinit var personName :String

    //Define uma data class, os tempos serão salvos dentro dela, o ID será o nome do user
    @IgnoreExtraProperties
    data class User(val times: List<String>? = null) { }
// ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //busca os id's no activity_main para serem atribuídos a suas variáveis distintas
        timeTextView = findViewById(R.id.chronometer)
        startButton = findViewById(R.id.startButton)
        resetButton = findViewById(R.id.resetButton)
        noteButton = findViewById(R.id.noteButton)
        timeButton = findViewById(R.id.times_button)
        timeLog = findViewById(R.id.timeLog)
        val campoNome: TextView = findViewById(R.id.textView2)

        //Pega com o SharedPreferences o dado enviado, no caso, o nome da pessoa, para mostrar na tela
        val sharedPreferences = applicationContext.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val valor = sharedPreferences.getString("name","desconhecido")


        if(valor!!.isEmpty()){
            campoNome.setText("Olá, desconhecido")
            personName = "Desconhecido";
        }else{
            campoNome.setText("Olá, "+valor)
            personName = valor;
        }


        //Bloco que pega o botão return pelo ID e encerra a activity quando nota que ele é clicado
        val button = findViewById<Button>(R.id.return_button)
        button.setOnClickListener {
            Toast.makeText(this, R.string.returning, Toast.LENGTH_SHORT).show()
            finish()
        }

        startButton.setOnClickListener {
            //Verifica se a variável Booleana é false ou true.
            //Caso seja false, significa estar parado.
            if (!isRunning) {

                //caso haja um tempo definido no cronometro, pós pause, utiliza o SystemClock.elapsedRealtime() - elapsedTime
                //SystemClock.elapsedRealtime() é uma funcionalidade que pega o tempo do sistema, corrido, a subtração é com a finalidade de mostrar o tempo que foi pausado.
                //Caso não haja, zerará, ou simplesmente continuará rodando, mesmo pausado, quando retornar, o tempo estará mais avançado.
                if (elapsedTime == 0L) {
                    startTime = SystemClock.elapsedRealtime()
                } else {
                    startTime = SystemClock.elapsedRealtime() - elapsedTime
                }

                //delay definido em 0 milisegundos
                handler.postDelayed(updateTimer, 0)

                //permite que os botões de reset e de note sejam utiliuzáveis, assim como muda o estado da varioável booleana para true
                resetButton.isEnabled = true
                noteButton.isEnabled = true
                isRunning = true
            }else{
                //remove a atualização do relógio, pausando ele. Sala o tempo decorrido, em relação ao tempo do sistema, para quando iniciar de novo
                //muda o estado da variável para falso, pois está parado
                handler.removeCallbacks(updateTimer)
                elapsedTime = SystemClock.elapsedRealtime() - startTime
                resetButton.isEnabled = true
                noteButton.isEnabled = true
                isRunning = false
            }
            //mostra um Toast a depender do estado da variável isRunnning, assim como muda o nome do botão start para pause ou vice e versa.
            Toast.makeText(this, getString(if(isRunning)R.string.working else R.string.stopped), Toast.LENGTH_SHORT).show()
            startButton.setText(if(!isRunning){R.string.start_button} else {R.string.pause_button})
        }


        resetButton.setOnClickListener {
            //Caso reset seja clicado, tira o callback que atualiza o timer e muda os valores para zero, assim como os valores de voltas anotados
            //Habilita apenas o botão de start para iniciar tudo de novo
            handler.removeCallbacks(updateTimer)
            elapsedTime = 0
            timeTextView.text = "00:00:000"
            timeLog.text = getString(R.string.time_log)
            startButton.isEnabled = true
            resetButton.isEnabled = false
            noteButton.isEnabled = false
            isRunning = false

            //--Bloco de salvar a pessoa e os tempos anotados no Firebase

            //pega a referencia do banco de dados no Firebase
            val database = Firebase.database("https://cronometro-c0889-default-rtdb.firebaseio.com/");
            //pega a referencia do "diretório/objeto" a salvar
            var myRef = database.getReference("users")

            //Instancia a Data Class criada acima
            val novoUsuario = User(times = timesList)

            //Define que vai salvar o nome do usuário como "chave" e os tempos salvos como "valores"
            myRef.child(personName).setValue(novoUsuario)
                .addOnSuccessListener {
                    Toast.makeText(this, R.string.succeded_upload, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, R.string.failed_upload, Toast.LENGTH_SHORT).show()
                }

            //---------------------------


            startButton.setText(if(!isRunning){R.string.start_button} else {R.string.pause_button})
        }

        noteButton.setOnClickListener {
            //Caso seja clicado o botão "volta", pega o valor atual, quando no click e os valores anteriores salvos, ou não, como toString
            //Então, formata eles para incluir quebra de linha, montando uma anotação estilo horizontal.
            val currentTime = timeTextView.text.toString()
            val previousTimeLog = timeLog.text.toString()
            val newTimeLog = "$previousTimeLog\n$currentTime"
            timeLog.text = newTimeLog

            //adiciona o tempo atual na lista mutável, para depois inserir no Firebase Storage
            timesList.add(currentTime);

            Toast.makeText(this, R.string.lap_count, Toast.LENGTH_SHORT).show()
        }

        timeButton.setOnClickListener{
            val intent = Intent(this, Times::class.java)
            startActivity(intent)
        }
    }

    //funcuionalidade utilizando o objeto updateTimer do tipo da classe Runnable, onde muda o estado do relógio a cada atualização.
    //atualização esta que foi definida no handler como sendo a cada 0 milisegundos. (incremento em milisegundos)
    private val updateTimer = object : Runnable {
        override fun run() {
            //primeiro pega a referência com o sistema, depois dá update(se vier do pause, utiliza o tempo pausado)
            //depois, formata, com base no tempo capturado, os millisegundos em segundos e minutos.
            val timeInMilliseconds = SystemClock.elapsedRealtime() - startTime
            val updatedTime = elapsedTime + timeInMilliseconds
            val seconds = (updatedTime / 1000).toInt()
            val minutes = seconds / 60
            val milliseconds = (updatedTime % 1000).toInt()
            val secondsDisplay = seconds % 60
            //define o formato do cronometro, com 3 casas no milisegundos, atribuindo para cada tipo de Integer, uma variável formatada
            val timerText =
                String.format("%02d:%02d:%03d", minutes, secondsDisplay, milliseconds)
            timeTextView.text = timerText
            //define o tempo de atualização, com delay de 0 milisegundos.
            handler.postDelayed(this, 0)
        }
    }
    /*@IgnoreExtraProperties
    data class Person(val username: String? = null, val times: List<String>? = null) {

    }*/
}


