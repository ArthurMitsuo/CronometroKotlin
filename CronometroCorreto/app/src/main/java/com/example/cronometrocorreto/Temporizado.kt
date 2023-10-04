package com.example.cronometrocorreto

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.os.Vibrator
import android.os.VibrationEffect
//import android.media.MediaPlayer

class Temporizado : AppCompatActivity() {

    //Inicialização das variáveis globais
    private lateinit var timeInput: EditText
    private lateinit var startButton: Button
    private lateinit var timerTextView: TextView

    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var secondsRemaining : Long = 0
    private var minutesRemaining : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temporizado)

        //busca no activity_temporizado os campos, pelo ID, e salva ele nas variáveis
        timeInput = findViewById(R.id.timeInput)
        startButton = findViewById(R.id.startButton)
        timerTextView = findViewById(R.id.timerTextView)

        startButton.setOnClickListener { startTimer() }

        val campoNome : TextView = findViewById(R.id.greetings)
        //chama o objeto Vibrador(que permite vibrar o celular - permissões extras colocadas no android manifest)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        //Bloco que pega o botão return pelo ID e encerra a activity quando nota que ele é clicado
        val button = findViewById<Button>(R.id.return_button)
        button.setOnClickListener {
            Toast.makeText(this, R.string.returning, Toast.LENGTH_SHORT).show()
            finish()
        }

        //busca no sharedpreferences "sharedPrefs" o valor colocado no campo Nome(chave "name") no menu e mostra no textView greetings
        val sharedPreferences = applicationContext.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val valor = sharedPreferences.getString("name","desconhecido")

        if(valor!!.isEmpty()){
            campoNome.setText("Olá, desconhecido")
        }else{
            campoNome.setText("Olá, "+valor)
        }
    }

    private fun startTimer() {
        //chama, de novo, o objeto Vibrador(que permite vibrar o celular - permissões extras colocadas no android manifest)
        //funcionou, resolvi não mexer, apesar de redundante
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        //mostra um toast quando clica no botão para iniciar, e mostra que já está rodando caso já esteja rodando
        Toast.makeText(this, getString(if(isTimerRunning)R.string.timer_warn else R.string.working), Toast.LENGTH_SHORT).show()

        if (!isTimerRunning) {
            //pega o valor do input no campo EditText, em segundos e salva na variável, convertendo depois para milisegundos
            val timeInSeconds = timeInput.text.toString().toLong()
            val timeInMillis = timeInSeconds * 1000

            //busca o objeto countdownTimer e, do objeto, chama o método Tick, que atualiza a cada segundo.
            countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    secondsRemaining = millisUntilFinished / 1000
                    minutesRemaining = secondsRemaining/60
                    val seconds = secondsRemaining%60

                    timerTextView.text = String.format("%02d:%02d", minutesRemaining, seconds)
                }
                //Também chama o método onFinish, que define quando o countdown terminou, mudando o texto e vibrando o celular
                override fun onFinish() {
                    timerTextView.text = "Tempo encerrado!"
                    isTimerRunning = false

                    //https://www.tutorialspoint.com/how-to-make-an-android-device-vibrate-programmatically-using-kotlin
                    //pega do objeto instanciado vibrator e verifica se o celular pode utilizar o método vibrar com efeito.
                    //nos dois modos, faz o celular vibrar por 1000 milisegundos
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(1000,
                            VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    else {
                        vibrator.vibrate(1000)
                    }
                }

            }
            //define o início do countdown e a variável de isTimerRunning para True. Antes ele apenas instancia os métodos.
            countDownTimer.start()
            isTimerRunning = true
        }

    }
}