package com.example.desafiofirebase

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.desafiofirebase.auth.login.LoginActivity
import com.example.desafiofirebase.listadejogos.HomeActivity
import com.google.firebase.auth.FirebaseAuth


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val authenticate = FirebaseAuth.getInstance()
        val currentUser = authenticate.currentUser

        if (currentUser != null) {

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)

        } else {

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)

        }

    }
}