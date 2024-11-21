package com.example.furmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var formRegister: View
    private lateinit var formLogin: View

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_login_reg)

        // initialize FirebaseAuth object
        auth = Firebase.auth

        formRegister = findViewById<View>(R.id.form_register)
        formLogin = findViewById<View>(R.id.form_login)

        handleCreateAccountPage(formRegister, formLogin)
        handleLoginPage(formRegister, formLogin)
        handleCreateAccount()
        handleLogin()
    }

    private fun handleLogin() {
        val loginButton = findViewById<Button>(R.id.sign_in_btn)
        loginButton.setOnClickListener {
            // prevent multiple clicks
            loginButton.isEnabled = false

            val email = formLogin.findViewById<TextInputEditText>(R.id.input_email).text.toString()
            val password = formLogin.findViewById<TextInputEditText>(R.id.input_password).text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Finish this activity so it cannot be accessed via back button
            }.addOnFailureListener { exception ->
                if (exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Login failure", exception.toString())
                }
            }

            loginButton.isEnabled = true
        }
    }

    private fun handleCreateAccount() {
        val createAccountButton = findViewById<Button>(R.id.create_account_btn)

        createAccountButton.setOnClickListener {
            // disable button
            createAccountButton.isEnabled = false

            // ensure that passwords are the same
            val username = formRegister.findViewById<TextInputEditText>(R.id.input_field).text.toString()
            val email = formRegister.findViewById<TextInputEditText>(R.id.input_email).text.toString()
            val password = formRegister.findViewById<TextInputEditText>(R.id.input_password).text.toString()
            val confirmPassword = formRegister.findViewById<TextInputEditText>(R.id.input_password2).text.toString()

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // create account
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                val user = auth.currentUser
                if (user == null) {
                    Toast.makeText(this, "Failed to log in. Please try again", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val db = Firebase.firestore
                val data = hashMapOf(
                    "username" to username,
                    "uid" to user.uid
                )
                db.collection("Users").add(data)
                    .addOnSuccessListener {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // Finish this activity so it cannot be accessed via back button
                    }
                    .addOnFailureListener {exception ->
                        Log.e("Firestore Error", exception.toString())
                    }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to create user. Please try again", Toast.LENGTH_SHORT).show()
            }

            createAccountButton.isEnabled = true
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
