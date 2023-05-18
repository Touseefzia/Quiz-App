package com.example.triviacompose.room_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.triviacompose.room_db.entity.Question
import com.example.triviacompose.room_db.type_converter.ArrayListConverter


@Database(
    entities = [Question::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(
    value = [
        ArrayListConverter::class,
    ]
)
abstract class QuestionDatabase: RoomDatabase() {
    abstract fun getQuestionDao(): QuestionDao

    companion object {
        private const val DB_NAME = "question_database.db"
        @Volatile private var instance: QuestionDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            QuestionDatabase::class.java,
            DB_NAME
        ).build()
    }
}