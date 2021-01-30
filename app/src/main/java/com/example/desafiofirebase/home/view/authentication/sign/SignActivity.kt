package com.example.desafiofirebase.auth.sign

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.desafiofirebase.R
import com.example.desafiofirebase.listadejogos.HomeActivity

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class SignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val authenticate = FirebaseAuth.getInstance()

        createAccountClick()

    }

    private fun createAccountClick(){

        findViewById<MaterialButton>(R.id.btnCreateAccountSign).setOnClickListener {

            val password = findViewById<TextInputEditText>(R.id.edtSignPassword)
            val repeatPassword = findViewById<TextInputEditText>(R.id.edtRepeatPassword)
            val name = findViewById<TextInputEditText>(R.id.edtName)
            val email = findViewById<TextInputEditText>(R.id.edtSignEmail)

            verifyUserCreate( password, repeatPassword,name, email)

        }

    }

    private fun verifyUserCreate(password: TextInputEditText, repeatPassword: TextInputEditText,name: TextInputEditText, email: TextInputEditText
    ) {

        val passText = password.text.toString()
        val repeatText = repeatPassword.text.toString()
        val nameText = name.text.toString()
        val emailText: String = email.text.toString()

        if (!nameText.isBlank()) {
            if (!emailText.isBlank()) {
                if (!passText.isBlank() && passText.length >= 6) {
                    if (repeatText == passText) {
                        createUser(nameText, emailText, passText)
                    } else {
                        repeatPassword.error = "Senhas não são iguais"
                    }
                } else {
                    password.error = "Senha tem que ter no minimo 6 caracteres"
                }
            } else {
                email.error = "O campo email não pode estar vazio"
            }
        } else {
            name.error = "O campo nome não pode estar vazio"
        }
    }

    private fun createUser(name: String, emailResults: String, passwordResults: String) {
        val authenticate = FirebaseAuth.getInstance()

        authenticate.createUserWithEmailAndPassword(emailResults, passwordResults)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build()
                    user!!.updateProfile(profileUpdates)
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }
}
