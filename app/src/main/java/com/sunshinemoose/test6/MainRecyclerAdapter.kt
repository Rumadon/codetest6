package com.sunshinemoose.test6

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainRecyclerAdapter(private var data: List<String>) : RecyclerView.Adapter<MainRecyclerAdapter.MainViewHolder>() {
    private var onItemClickListener: ((shipment: String) -> Unit)? = null

    fun setItems(order: List<String>) {
        data = order
        notifyDataSetChanged()
    }

    fun setItemClickListener(listener: (shipment: String) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.text_list_item, parent, false)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val shipment = data[position]
        holder.content.text = shipment
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(shipment)
        }
    }

    inner class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView = view.findViewById(R.id.content)
    }

}
