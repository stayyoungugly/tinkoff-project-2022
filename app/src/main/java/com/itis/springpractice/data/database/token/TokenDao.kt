package com.itis.springpractice.data.database.token

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itis.springpractice.data.database.token.model.Token

@Dao
interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveToken(token: Token)

    @Query("SELECT * FROM tokens WHERE id = :id")
    suspend fun findTokenById(id: Int): Token

    @Query("SELECT * FROM tokens WHERE token = :token")
    suspend fun findToken(token: String): Token

    @Query("SELECT * FROM tokens")
    fun findAllTokens(): List<Token>

    @Query("DELETE FROM tokens WHERE id = :id")
    fun deleteTokenById(id: Int)

    @Query("DELETE FROM tokens")
    fun deleteAllTokens()
}
