package com.example.desafiofirebase.detalhesjogo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.desafiofirebase.R
import com.example.desafiofirebase.creategame.RegisterAGameActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class InfoGameActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infogame)

        val myContainer = findViewById<TextView>(R.id.txtDetailsDescription)
        val name = intent.getStringExtra("name")!!
        val dateCreated = intent.getStringExtra("dateCreated")!!
        val description = intent.getStringExtra("description")!!
        val imgURL = intent.getStringExtra("imgURL")!!

        findViewById<TextView>(R.id.txtTitleGame).text = name
        findViewById<TextView>(R.id.txtYearDetails).text = "Lançamento: $dateCreated"
        findViewById<TextView>(R.id.txtNameDetails).text = name

        myContainer.text = description
        myContainer.movementMethod = ScrollingMovementMethod()

        loadingImageGame(imgURL, findViewById(R.id.imgViewDetails))

        findViewById<ImageView>(R.id.iconReturn).setOnClickListener {
            finish()
        }

        clicFloatButton(name,dateCreated,description,imgURL)
    }

    private fun clicFloatButton(name:String,dateCreated:String,description:String,imgURL:String){
        findViewById<FloatingActionButton>(R.id.btnEditGame).setOnClickListener {
            val intent = Intent(this, RegisterAGameActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("dateCreated", dateCreated)
            intent.putExtra("description", description)
            intent.putExtra("imgURL", imgURL)
            startActivity(intent)
            finish()
        }

    }

    private fun loadingImageGame(imgPath: String, containerGame: ImageView?) {
        val storageDB = FirebaseStorage.getInstance().getReference("uploads")
        storageDB.child(imgPath).downloadUrl.addOnSuccessListener {
            Picasso.get()
                .load(it)
                .into(containerGame)
        }
    }
}