package com.nanioi.calculator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
        @PrimaryKey val uid: Int?,
        @ColumnInfo(name = "expression") val expression: String?,
        @ColumnInfo(name = "result") val result: String?
)
//room 데이터클라스로 변경