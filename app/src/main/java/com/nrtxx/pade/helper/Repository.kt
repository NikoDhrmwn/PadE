package com.nrtxx.pade.helper

import androidx.lifecycle.LiveData
import com.nrtxx.pade.db.History
import com.nrtxx.pade.db.HistoryDao

class Repository(private val historyDao: HistoryDao) {
    val readHistory: LiveData<List<History>> = historyDao.getAllHistory()

    fun insertHistory(history: History) {
        historyDao.insert(history)
    }
}