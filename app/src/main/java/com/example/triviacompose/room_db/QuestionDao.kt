package com.example.triviacompose.room_db

import androidx.room.*
import com.example.triviacompose.room_db.entity.Question
import kotlinx.coroutines.flow.Flow


@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun deleteQuestion(question: Question)

    @Query("SELECT * FROM question_table ORDER BY type DESC")
    fun getAllQuestions(): Flow<List<Question>>

    @Query("SELECT sum(achieved_score) FROM question_table")
    fun getTillDatePoints(): Flow<Int?>

    @Query("DELETE FROM question_table")
    suspend fun clearQuestionTable()

    @Query("DELETE FROM question_table WHERE id = :id")
    suspend fun deleteQuestionById(id: Int)
}