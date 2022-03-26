package com.itis.springpractice.data.database.token.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokens")
data class Token(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "id_token")
    val idToken: String,
    @ColumnInfo(name = "refresh_token")
    val refreshToken: String
)
