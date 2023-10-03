package com.example.cronometrocorreto
//https://developer.android.com/training/data-storage/shared-preferences?hl=pt-br
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
    var tempoPausado = 0;
    var tempoResetado = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cronometro = findViewById<Chronometer>(R.id.chronometer)
        val btnStart = findViewById<Button>(R.id.startButton)
        val btnReset = findViewById<Button>(R.id.resetButton)
        var isWorking = false
        var resetado = false

        btnStart.setOnClickListener(){
            if(!isWorking){
                cronometro.base = SystemClock.elapsedRealtime()-tempoPausado
                cronometro.start()
                isWorking = true
            }else{
                cronometro.stop()
                tempoPausado = (SystemClock.elapsedRealtime() - cronometro.base).toInt()
                isWorking = false
            }
            btnStart.setText(if(!isWorking){R.string.start_button} else {R.string.pause_button})
            Toast.makeText(this, getString(if(isWorking)R.string.working else R.string.stopped), Toast.LENGTH_SHORT).show()
        }

        btnReset.setOnClickListener(){
            cronometro.stop()
            cronometro.base = SystemClock.elapsedRealtime()
            isWorking = false
            resetado = true

            btnReset.setText(R.string.reset_button)
            btnStart.setText(if(!isWorking){R.string.start_button} else {R.string.pause_button})
            Toast.makeText(this, getString(if(resetado)R.string.working else R.string.stopped), Toast.LENGTH_SHORT).show()
            resetado = false
        }
    }
}


