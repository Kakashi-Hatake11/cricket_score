package com.example.cricketscore.ui.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.cricketscore.database.HistoryDatabase
import com.example.cricketscore.database.MatchHistory
import com.example.cricketscore.repository.HistoryRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class GameScorerViewModel(context: Application) : AndroidViewModel(context) {

    private var parentJob = SupervisorJob()
    var repository = HistoryRepository(HistoryDatabase.getInstance(context))

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun insertMatchHistory(matchHistory: MatchHistory) {
        scope.launch {
            withContext(Dispatchers.IO) {
                repository.insertMatchHistory(matchHistory)
            }
        }
    }


}