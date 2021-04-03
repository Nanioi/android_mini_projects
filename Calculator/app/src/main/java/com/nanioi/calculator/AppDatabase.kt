package com.nanioi.calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nanioi.calculator.dao.HistoryDao
import com.nanioi.calculator.model.History

@Database(entities = [History::class],version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun historyDao():HistoryDao
}