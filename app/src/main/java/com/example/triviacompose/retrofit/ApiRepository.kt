package com.example.triviacompose.retrofit

import android.util.Log
import com.example.triviacompose.models.TriviaResponse
import com.example.triviacompose.viewmodels.MainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository constructor(private val retrofitService: RetrofitService,private val viewModel: MainViewModel) {
    val TAG = "ApiRepository"

    fun getQuestionsByFilter(params: Map<String, String>) {
        val response = retrofitService.getQuestionsByFilter(params = params)
        response.enqueue(object : Callback<TriviaResponse> {
            override fun onResponse(call: Call<TriviaResponse>, response: Response<TriviaResponse>) {
                val result: TriviaResponse? = response.body()
                result?.let { res->
                    viewModel.questionList.clear()
                    viewModel.questionList.addAll(res.results)
                }
                Log.d(TAG, "questionList size = ${result?.results?.size} AND  Data = ${result?.results}")
            }

            override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
                viewModel.errorMessage.postValue(t.message)
            }
        })
    }


}