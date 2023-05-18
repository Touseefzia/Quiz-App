package com.example.triviacompose.room_db.entity

import androidx.room.*
import com.google.gson.annotations.SerializedName


@Entity(
    tableName = "question_table"
)
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("question") @ColumnInfo(name = "question") val question: String? = null,
    @SerializedName("category") @ColumnInfo(name = "category") val category: String? = null,
    @SerializedName("type") @ColumnInfo(name = "type") val type: String? = null,
    @SerializedName("difficulty") @ColumnInfo(name = "difficulty") val difficulty: String? = null,
    @SerializedName("correct_answer") @ColumnInfo(name = "correct_answer") val correctAnswer: String? = null,
    @SerializedName("incorrect_answers") @ColumnInfo(name = "incorrect_answers") val incorrectAnswers: ArrayList<String>? = null,
    @ColumnInfo(name = "achieved_score") var achievedScore: Int? = 0,
)




