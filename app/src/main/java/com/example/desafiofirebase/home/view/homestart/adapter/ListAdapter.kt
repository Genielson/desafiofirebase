package com.example.desafiofirebase.listadejogos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiofirebase.R
import com.example.desafiofirebase.model.MyGameModel
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ListAdapter(private val list: MutableList<MyGameModel>, private val listener: (MyGameModel) -> Unit
) : RecyclerView.Adapter<ListAdapter.MyListViewHolder>() {

    class MyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val image = view.findViewById<ImageView>(R.id.imageViewJogo)
        private val year = view.findViewById<TextView>(R.id.txtAnoJogo)
        private val name = view.findViewById<TextView>(R.id.txtNomeJogo)

        fun bind(mygame: MyGameModel) {
            name.text = mygame.name
            year.text = mygame.dateCreated

            val storageDB = FirebaseStorage.getInstance().getReference("uploads")
            storageDB.child(mygame.imgGame).downloadUrl.addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .into(image)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_listgame, parent, false)
        return MyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { listener(item) }
    }

    override fun getItemCount() = list.size
}