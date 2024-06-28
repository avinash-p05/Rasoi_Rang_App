package com.example.recipesharingappxml.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.recipesharingappxml.R

class start : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val start : Button = findViewById(R.id.startbtn)
        val intent = Intent(this, Login::class.java)
        start.setOnClickListener(View.OnClickListener {
            startActivity(intent)
        })

    }
}