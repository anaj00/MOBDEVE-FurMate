package com.example.furmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_login_reg)


        // Assuming there's a login button with id 'btn_login'
        val loginButton = findViewById<Button>(R.id.login_btn) // Replace with the actual button ID
        loginButton.setOnClickListener {
            // Once login is successful, start the InApp activity
            val intent = Intent(this, InApp::class.java)
            startActivity(intent)
            finish() // Optional: finish this activity so it cannot be accessed via back button
        }
    }
}
