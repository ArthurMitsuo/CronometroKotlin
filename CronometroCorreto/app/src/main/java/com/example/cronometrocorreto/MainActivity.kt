package com.example.cronometrocorreto

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

class MainActivity : AppCompatActivity() {
    var seconds:Int = 0
    var running:Boolean = false
    var lastRunningState:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val meter = findViewById<Chronometer>(R.id.chronometer)
        val btnStart = findViewById<Button>(R.id.startButton)
        val btnReset = findViewById<Button>(R.id.resetButton)
        var isWorking = false

        btnStart.setOnClickListener(){
            if(!isWorking){
                meter.start()
                isWorking = true
            }else{
                meter.stop()
                //meter.base = SystemClock.elapsedRealtime()
                isWorking = false
            }
            btnStart.setText(if(!isWorking){R.string.start_button} else {R.string.pause_button})
            Toast.makeText(this, getString(if(isWorking)R.string.working else R.string.stopped), Toast.LENGTH_SHORT).show()
        }

        btnReset.setOnClickListener(){
            if(isWorking){
                //meter.stop()
                meter.base = SystemClock.elapsedRealtime()
                isWorking = false
            }else{
                meter.stop()
                meter.base = SystemClock.elapsedRealtime()
                isWorking = false
            }
            btnReset.setText(R.string.reset_button)

        }
        //novo bloco
        if(savedInstanceState != null){
            seconds=savedInstanceState.getInt("seconds")
            running=savedInstanceState.getBoolean("running")
            lastRunningState=savedInstanceState.getBoolean("lastRunningState")
        }

        runStopWatch()

    }
    private fun runStopWatch(){
        var txtTime=findViewById<TextView>(R.id.textView)

        val handler=Handler()

        handler.post(
            Runnable {
                run {
                    var hours=seconds/3600
                    var minutes=(seconds%3600)/60
                    var secpmds=seconds/3600
                }
        })
    }

    fun onClickStart(view: android.view.View){
        running = true
    }
    fun onClickStop(view: android.view.View){
        running = false
    }
    fun onClickReset(view: android.view.View){
        running = true
        seconds = 0
    }
}

