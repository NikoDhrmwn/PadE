package com.nrtxx.pade.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nrtxx.pade.databinding.ItemRowHistoryBinding

class HistoryAdapter(private val listHistory: ArrayList<History>): RecyclerView.Adapter<HistoryAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val history = listHistory[position]
        Glide.with(holder.itemView.context)
            .load(history.photo)
            .into(holder.binding.imgItemPhoto)
        holder.binding.tvItemName.text = history.name
        holder.binding.tvItemDescription.text = history.description
    }

    override fun getItemCount(): Int = listHistory.size

    class ListViewHolder(var binding: ItemRowHistoryBinding) : RecyclerView.ViewHolder(binding.root)
}