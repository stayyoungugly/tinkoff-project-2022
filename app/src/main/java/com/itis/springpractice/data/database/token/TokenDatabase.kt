package com.itis.springpractice.data.database.token

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.itis.springpractice.data.database.token.model.Token

@Database(
    entities = [Token::class],
    version = 1,
    exportSchema = true
)
abstract class TokenDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao

    companion object {
        private const val DATABASE_NAME = "tokens.db"
        private lateinit var instance: TokenDatabase

        fun getInstance(context: Context): TokenDatabase {
            if (!::instance.isInitialized) {
                synchronized(TokenDatabase::class) {
                    if (!::instance.isInitialized) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            TokenDatabase::class.java,
                            DATABASE_NAME
                        ).build()
                    }
                }
            }
            return instance
        }
    }
}
