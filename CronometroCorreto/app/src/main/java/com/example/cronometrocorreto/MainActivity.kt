package com.example.cronometrocorreto
//https://developer.android.com/training/data-storage/shared-preferences?hl=pt-br
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
import org.w3c.dom.Text
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var timeTextView: TextView
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var noteButton: Button
    private lateinit var timeLog: TextView
    private var isRunning = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private val handler = Handler()

    val SHARED: String = "sharedPrefs"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeTextView = findViewById(R.id.chronometer)
        startButton = findViewById(R.id.startButton)
        resetButton = findViewById(R.id.resetButton)
        noteButton = findViewById(R.id.noteButton)
        timeLog = findViewById(R.id.timeLog)
        val campoNome: TextView = findViewById(R.id.textView2)

        val sharedPreferences = applicationContext.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val valor = sharedPreferences.getString("name","desconhecido")

        if(valor!!.isEmpty()){
            campoNome.setText("Olá, desconhecido")
        }else{
            campoNome.setText("Olá, "+valor)
        }


        val button = findViewById<Button>(R.id.return_button)
        button.setOnClickListener {
            Toast.makeText(this, R.string.returning, Toast.LENGTH_SHORT).show()
            finish()
        }

        startButton.setOnClickListener {
            if (!isRunning) {
                if (elapsedTime == 0L) {
                    startTime = SystemClock.elapsedRealtime()
                } else {
                    startTime = SystemClock.elapsedRealtime() - elapsedTime
                }
                handler.postDelayed(updateTimer, 0)
                resetButton.isEnabled = true
                noteButton.isEnabled = true
                isRunning = true
            }else{
                handler.removeCallbacks(updateTimer)
                elapsedTime = SystemClock.elapsedRealtime() - startTime
                resetButton.isEnabled = true
                noteButton.isEnabled = true
                isRunning = false
            }
            Toast.makeText(this, getString(if(isRunning)R.string.working else R.string.stopped), Toast.LENGTH_SHORT).show()
            startButton.setText(if(!isRunning){R.string.start_button} else {R.string.pause_button})
        }


        resetButton.setOnClickListener {
            handler.removeCallbacks(updateTimer)
            elapsedTime = 0
            timeTextView.text = "00:00:000"
            timeLog.text = getString(R.string.time_log)
            startButton.isEnabled = true
            resetButton.isEnabled = false
            noteButton.isEnabled = false
            isRunning = false

            Toast.makeText(this, R.string.reset, Toast.LENGTH_SHORT).show()
            startButton.setText(if(!isRunning){R.string.start_button} else {R.string.pause_button})
        }

        noteButton.setOnClickListener {
            val currentTime = timeTextView.text.toString()
            val previousTimeLog = timeLog.text.toString()
            val newTimeLog = "$previousTimeLog\n$currentTime"
            timeLog.text = newTimeLog

            Toast.makeText(this, R.string.lap_count, Toast.LENGTH_SHORT).show()
        }
    }

    private val updateTimer = object : Runnable {
        override fun run() {
            val timeInMilliseconds = SystemClock.elapsedRealtime() - startTime
            val updatedTime = elapsedTime + timeInMilliseconds
            val seconds = (updatedTime / 1000).toInt()
            val minutes = seconds / 60
            val milliseconds = (updatedTime % 1000).toInt()
            val secondsDisplay = seconds % 60
            val timerText =
                String.format("%02d:%02d:%03d", minutes, secondsDisplay, milliseconds)
            timeTextView.text = timerText
            handler.postDelayed(this, 0)
        }
    }
}


