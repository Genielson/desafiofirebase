package com.example.desafiofirebase.listadejogos

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiofirebase.R
import com.example.desafiofirebase.creategame.RegisterAGameActivity
import com.example.desafiofirebase.detalhesjogo.InfoGameActivity
import com.example.desafiofirebase.listadejogos.adapter.ListAdapter
import com.example.desafiofirebase.model.MyGameModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {

    private var _myList = mutableListOf<MyGameModel>()
    private lateinit var _adapter: ListAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        val mydatabase = FirebaseDatabase.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val references = mydatabase.getReference(user!!.uid)

        references.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                _myList.clear()

                p0.children.forEach {
                    val mygame = it.getValue(MyGameModel::class.java)
                    _myList.add(mygame!!)
                }
                getMyAllDatas()
            }

            override fun onCancelled(p0: DatabaseError) {
                references.removeEventListener(this)
            }

        })

        clickFloatButton()
    }

    @RequiresApi(Build.VERSION_CODES.O)

    private fun clickFloatButton(){
        findViewById<FloatingActionButton>(R.id.btnRegistrarJogo).setOnClickListener {
            val intent = Intent(this, RegisterAGameActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getMyAllDatas() {

        val layout = GridLayoutManager(this, 2)
        val recycler = findViewById<RecyclerView>(R.id.recyclerViewGames)

        if (_myList.isNotEmpty()) {

            _adapter = ListAdapter(_myList) {
                    val intent = Intent(this, InfoGameActivity::class.java)

                    intent.putExtra("dateCreated", it.dateCreated)
                    intent.putExtra("description", it.description)
                    intent.putExtra("name", it.name)
                    intent.putExtra("imageURL", it.imgGame)
                    startActivity(intent)
            }
            recycler.apply {
                this.adapter = _adapter
                this.layoutManager = layout
            }
        }
    }
}