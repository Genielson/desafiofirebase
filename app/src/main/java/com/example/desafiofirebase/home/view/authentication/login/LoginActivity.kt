package com.example.desafiofirebase.authenticate.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.desafiofirebase.R
import com.example.desafiofirebase.authenticate.sign.SignActivity
import com.example.desafiofirebase.listadejogos.HomeActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<MaterialButton>(R.id.btnCreateAccount).setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnLogin).setOnClickListener {
            val emailContainer = findViewById<TextInputEditText>(R.id.edtLoginEmail)
            val passwordContainer = findViewById<TextInputEditText>(R.id.edtLoginPassword)

            checkedLogin(emailContainer, passwordContainer)
        }

        val preferences: SharedPreferences = this.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val email = preferences.getString("Email", "")
        val password = preferences.getString("Senha", "")

        if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
            findViewById<TextInputEditText>(R.id.edtLoginEmail).setText(email)
            findViewById<TextInputEditText>(R.id.edtLoginPassword).setText(password)
        }

    }


    private fun checkedLogin(
        emailInput: TextInputEditText?,
        passwordInput: TextInputEditText?
    ) {

        val email = emailInput!!.text.toString()
        val password = passwordInput!!.text.toString()

        if (!email.isBlank()) {
            if (!password.isBlank()) {
                val remember = findViewById<CheckBox>(R.id.checkBoxRemember).isChecked

                val auth = FirebaseAuth.getInstance()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, HomeActivity::class.java)
                            val preferences: SharedPreferences =
                                this.getSharedPreferences("Login", Context.MODE_PRIVATE)
                            if (remember) {
                                preferences.edit().putString("Email", email)
                                    .putString("Senha", password).apply()
                            } else {
                                preferences.edit().clear().apply()
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Credenciais incorretas", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                passwordInput.error = getString(R.string.password_vazio)
            }
        } else {
            emailInput.error = getString(R.string.email_vazio)
        }
    }


}