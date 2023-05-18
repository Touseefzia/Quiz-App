package com.example.triviacompose.models

import com.example.triviacompose.room_db.entity.Question
import com.google.gson.annotations.SerializedName

data class TriviaResponse (
    @SerializedName("response_code" ) val responseCode : Int?,
    @SerializedName("results") val results: ArrayList<Question>
)