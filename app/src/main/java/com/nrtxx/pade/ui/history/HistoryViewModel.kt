package com.nrtxx.pade.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nrtxx.pade.db.History
import com.nrtxx.pade.db.HistoryRoomDatabase
import com.nrtxx.pade.helper.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application): AndroidViewModel(application) {
    private val dao = HistoryRoomDatabase.getDatabase(application).historyDao()
    private val repository = Repository(dao)

    val readHistory: LiveData<List<History>> = repository.readHistory

    fun insertHistory(history: History) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertHistory(history)
        }
    }
}