@file:Suppress("CanBeVal")

package com.example.desafiofirebase.creategame

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.example.desafiofirebase.R
import com.example.desafiofirebase.listadejogos.HomeActivity
import com.example.desafiofirebase.model.MyGameModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circleview.CircleView
import com.squareup.picasso.Picasso

class RegisterAGameActivity : AppCompatActivity() {

    private var _fileSaved: String? = null
    private var _firstName: String? = null
    private var _imageURI: Uri? = null
    private var _firstImageURI: Uri? = null

    val imagemContainer = findViewById<ImageView>(R.id.imagemSelecionada)
    val nameInput = findViewById<EditText>(R.id.edtNameJogo)
    val dateInput = findViewById<EditText>(R.id.edtDateJogo)
    val descriptionInput = findViewById<EditText>(R.id.edtDescriptionOfTheGame)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creategame)

        _firstName = intent.getStringExtra("name")
        val dateCreated = intent.getStringExtra("dateCreated")
        val description = intent.getStringExtra("description")
        _fileSaved = intent.getStringExtra("imgURL")

        findViewById<CircleView>(R.id.btnUpload).setOnClickListener {
            searchFiles()
        }
        findViewById<ImageView>(R.id.imageUpload).setOnClickListener {
            searchFiles()
        }

        if (!_firstName.isNullOrBlank()) {

            nameInput.setText(_firstName)
            dateInput.setText(dateCreated)
            descriptionInput.setText(description)
            loadingInContent(_fileSaved!!, imagemContainer)

            findViewById<MaterialButton>(R.id.btnSaveGame).setOnClickListener {
                verifyInputsEmpty(nameInput, dateInput, descriptionInput)
            }

        } else {
            findViewById<MaterialButton>(R.id.btnSaveGame).setOnClickListener {
                verifyInputsEmpty(nameInput, dateInput, descriptionInput)
            }
        }

    }

    private fun searchFiles() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    private fun verifyInputsEmpty(nameResults: EditText, dateResults: EditText, descriptionResults: EditText
    ) {

        val nameCreated = nameResults.text.toString()
        val dateCreated = dateResults.text.toString()
        val descriptionCreated = descriptionResults.text.toString()

        if (!nameCreated.isBlank() && !dateCreated.isBlank() && !descriptionCreated.isBlank()) {

            if (_imageURI != null) {
                if (_imageURI == _firstImageURI) {
                    saveGame(nameCreated, dateCreated, descriptionCreated)
                } else {
                    enviarImagemEArmazenarNoDb(nameCreated, dateCreated, descriptionCreated)
                }
            }
        } else {
            makeText(this, "Preencha todos os campos, por favor! ", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            _imageURI = data?.data
            val img = findViewById<ImageView>(R.id.imagemSelecionada)
            changeImageGame(img)
        }
    }

    private fun changeImageGame(img: ImageView) {
        img.setImageURI(_imageURI)
        img.visibility = View.VISIBLE
        findViewById<ImageView>(R.id.imageUpload).visibility = View.GONE
    }

    private fun enviarImagemEArmazenarNoDb(nome: String, data: String, descricao: String) {
        _imageURI?.run {

            val user = FirebaseAuth.getInstance().currentUser
            val firebase = FirebaseStorage.getInstance()
            val storage = firebase.getReference("uploads")

            val extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(_imageURI!!))

            val fileName = "${user!!.uid}-${System.currentTimeMillis()}.${extension}"
            val fileReference = storage.child(fileName)

            fileReference.putFile(this)
                .addOnSuccessListener {
                    makeText(this@RegisterAGameActivity,
                        "Imagem carregada!",
                        Toast.LENGTH_SHORT
                    ).show()
                    _fileSaved = fileName
                    saveGame(nome, data, descricao)
                }
                .addOnFailureListener {
                    makeText(this@RegisterAGameActivity, it.toString(), Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                    Log.d("PROGRESS", it.toString())
                }
        }
    }

    private fun saveGame(nome: String, data: String, descricao: String) {
        if (_fileSaved != null) {
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseDatabase.getInstance()
            val filtered = ".,;:#"
            val nomeFormatado = nome.filterNot { filtered.indexOf(it) >-1}
            val ref = db.getReference(user!!.uid).child(nomeFormatado)

            if (!_firstName.isNullOrEmpty() && _firstName != nome) {
                _firstName = _firstName!!.filterNot { filtered.indexOf(it)>-1 }
                db.getReference(user.uid).child(_firstName!!).removeValue()
            }

            ref.setValue(MyGameModel(nome, data, descricao, _fileSaved!!))
                .addOnFailureListener {
                    makeText(this@RegisterAGameActivity, it.toString(), Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    val intent =
                        Intent(this@RegisterAGameActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        }
    }

    private fun loadingInContent(imageResultsName: String, myContainer: ImageView?) {
        val storageDB = FirebaseStorage.getInstance().getReference("uploads")
        storageDB.child(imageResultsName).downloadUrl.addOnSuccessListener {
            _imageURI = it
            _firstImageURI = it
            changeImageGame(myContainer!!)
            Picasso.get()
                .load(it)
                .into(myContainer)
        }
    }

}