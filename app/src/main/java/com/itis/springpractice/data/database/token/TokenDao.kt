package com.itis.springpractice.data.database.token

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itis.springpractice.data.database.token.model.Token

@Dao
interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveToken(idToken: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRefreshToken(refreshToken: String)

    @Query("SELECT * FROM tokens WHERE id_token = :token")
    suspend fun findToken(token: String): Token

    @Query("SELECT * FROM tokens WHERE refresh_token = :token")
    suspend fun findRefreshToken(token: String): Token

    @Query("SELECT * FROM tokens")
    fun findAllTokens(): List<Token>


}
