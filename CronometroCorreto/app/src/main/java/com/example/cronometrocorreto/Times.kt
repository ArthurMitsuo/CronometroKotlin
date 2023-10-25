package com.example.cronometrocorreto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Times : AppCompatActivity() {
    private lateinit var returnButton: Button
    private lateinit var eraseButton: Button
    private lateinit var times: TextView

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.getReference()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_times)

        returnButton = findViewById(R.id.return_button);
        eraseButton = findViewById(R.id.erase_button);
        times = findViewById(R.id.times);

        var peopleRef = storageRef.child("file/\\w+.jpg")

        val localFile = File.createTempFile("people", "json")
        var fileContent: Object

        peopleRef.getFile(localFile).addOnSuccessListener {
            Toast.makeText(this, R.string.succeded_download, Toast.LENGTH_SHORT).show();
            times.setText(localFile.readText())
        }.addOnFailureListener {
            // Handle any errors
            Toast.makeText(this, R.string.failed_download, Toast.LENGTH_SHORT).show();
        }

        returnButton.setOnClickListener{
            Toast.makeText(this, R.string.returning, Toast.LENGTH_SHORT).show();
            finish();
        }

        eraseButton.setOnClickListener{
            peopleRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, R.string.succeded_erase, Toast.LENGTH_SHORT).show();
                }.addOnFailureListener {
                    // Handle any errors
                    Toast.makeText(this, R.string.failed_erase, Toast.LENGTH_SHORT).show();
                }
        }
    }
}