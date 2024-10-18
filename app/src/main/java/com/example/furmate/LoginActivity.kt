package com.example.furmate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_login_reg)

        val formRegister = findViewById<View>(R.id.form_register)
        val formLogin = findViewById<View>(R.id.form_login)

        handleCreateAccountPage(formRegister, formLogin)
        handleLoginPage(formRegister, formLogin)
        handleCreateAccount()
        handleLogin()


    }

    private fun handleLogin() {
        val loginButton = findViewById<Button>(R.id.sign_in_btn)
        loginButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Finish this activity so it cannot be accessed via back button
        }
    }

    private fun handleCreateAccount() {
        val createAccountButton = findViewById<Button>(R.id.create_account_btn)
        createAccountButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Finish this activity so it cannot be accessed via back button
        }
    }

    private fun handleCreateAccountPage(formRegister: View, formLogin: View) {
        val createAccountButton = findViewById<TextView>(R.id.create_account_text)

        createAccountButton.setOnClickListener {
            formLogin.visibility = View.GONE
            formRegister.visibility = View.VISIBLE
        }
    }

    private fun handleLoginPage(formRegister: View, formLogin: View) {
        val loginButton = findViewById<TextView>(R.id.login_text)

        loginButton.setOnClickListener {
            formRegister.visibility = View.GONE
            formLogin.visibility = View.VISIBLE
        }
    }
}
