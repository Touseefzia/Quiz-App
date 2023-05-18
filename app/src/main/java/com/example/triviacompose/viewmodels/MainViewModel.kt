package com.example.triviacompose.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.triviacompose.app_constants.ApiPreferences
import com.example.triviacompose.app_constants.Category
import com.example.triviacompose.app_constants.Difficulty
import com.example.triviacompose.app_constants.Type
import com.example.triviacompose.retrofit.ApiRepository
import com.example.triviacompose.retrofit.RetrofitService
import com.example.triviacompose.room_db.QuestionDatabase
import com.example.triviacompose.room_db.entity.Question

class MainViewModel(private val questionDatabase: QuestionDatabase ) : ViewModel() {

    var questionList = mutableStateListOf<Question>()

    val errorMessage = MutableLiveData<String>()

    private val repository: ApiRepository = ApiRepository(RetrofitService.getInstance(),this )



    suspend fun insertQuestion(question: Question) = questionDatabase.getQuestionDao().insertQuestion(question = question)

    fun getTillDatePoints() = questionDatabase.getQuestionDao().getTillDatePoints()

    fun getQuestionsByFilter() = repository.getQuestionsByFilter(
        params = HashMap<String, String>().apply {
            this["amount"] = "15"
            this["category"] = ApiPreferences.selectedQuestionCategory.value.index.toString()
            this["difficulty"] = ApiPreferences.selectedQuestionDifficulty.value.paramValue
            this["type"] = ApiPreferences.selectedQuestionType.value.paramValue
        }.toMap()
    )

}