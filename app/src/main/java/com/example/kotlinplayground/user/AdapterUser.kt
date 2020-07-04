package com.example.kotlinplayground.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinplayground.R
import com.example.kotlinplayground.model.NewArrivals
import kotlinx.android.synthetic.main.content_item.view.*

class AdapterUser(private val list: List<NewArrivals>) :
    RecyclerView.Adapter<AdapterUser.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_item, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.name.text = list[position].name
        holder.itemView.model.text = list[position].model
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}