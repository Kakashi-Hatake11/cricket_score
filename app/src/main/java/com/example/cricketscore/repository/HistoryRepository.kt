package com.example.cricketscore.repository

import androidx.lifecycle.LiveData
import com.example.cricketscore.database.HistoryDatabase
import com.example.cricketscore.database.MatchHistory


class HistoryRepository(val database: HistoryDatabase) {

    fun insertMatchHistory(history: MatchHistory) {
        database.matchHistoryDAO.insert(history)
    }

    fun getMatchHistory(): List<MatchHistory> {
        return database.matchHistoryDAO.getAll()
    }
}