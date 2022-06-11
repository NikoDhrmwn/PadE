package com.nrtxx.pade.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nrtxx.pade.databinding.ItemRowHistoryBinding
import com.nrtxx.pade.db.History
import com.nrtxx.pade.helper.DiffCallback

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.ListViewHolder>() {

    private var history = ArrayList<History>()

    fun setListHistory(history: List<History>) {
        val diffCallback = DiffCallback(this.history, history)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.history.clear()
        this.history.addAll(history)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val history = history[position]
        Glide.with(holder.itemView.context)
            .load(history.image)
            .into(holder.binding.imgItemPhoto)
        holder.binding.tvItemName.text = history.penyakit
        holder.binding.tvItemDescription.text = history.date
    }

    override fun getItemCount(): Int = history.size

    class ListViewHolder(var binding: ItemRowHistoryBinding) : RecyclerView.ViewHolder(binding.root)
}