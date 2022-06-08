package com.nrtxx.pade.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nrtxx.pade.R
import com.nrtxx.pade.databinding.FragmentHistoryBinding
import com.nrtxx.pade.ui.history.History
import com.nrtxx.pade.ui.history.HistoryAdapter

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val list = ArrayList<History>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        binding.rvHistory.addItemDecoration(itemDecoration)

        list.addAll(listHistories)
        setHistory()
    }

    private val listHistories: ArrayList<History>
        get() {
            val dataName = resources.getStringArray(R.array.data_name)
            val dataDescription = resources.getStringArray(R.array.data_description)
            val dataPhoto = resources.getStringArray(R.array.data_photo)
            val listHistory = ArrayList<History>()
            for (i in dataName.indices) {
                val history = History(dataName[i], dataDescription[i], dataPhoto[i])
                listHistory.add(history)
            }
            return listHistory
        }

    private fun setHistory() {
        val adapter = HistoryAdapter(list)
        binding.rvHistory.adapter = adapter
    }

}