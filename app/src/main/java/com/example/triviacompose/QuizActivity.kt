package com.example.triviacompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.ComponentDialog
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.triviacompose.app_constants.ApiPreferences
import com.example.triviacompose.app_constants.Point
import com.example.triviacompose.room_db.QuestionDatabase
import com.example.triviacompose.ui.theme.TriviaComposeTheme
import com.example.triviacompose.viewmodels.MainViewModel
import kotlinx.coroutines.*

class QuizActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            mainViewModel = MainViewModel(QuestionDatabase.invoke(LocalContext.current))
            mainViewModel.getQuestionsByFilter()
            TriviaComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()/*.padding(all = 15.dp)*/,
                    color = MaterialTheme.colorScheme.background
                ) {
                    if(ApiPreferences.isQuickMode.value){
                        QuickModeQuiz()
                    }else{
                        NormalQuiz()
                    }
                }
            }
        }
    }



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun QuickModeQuiz() {
        var timer by remember { mutableStateOf(5000) }
        var activeIndex by remember { mutableStateOf(0) }
        var remainingLives by remember { mutableStateOf(3) }
        var totalScore by remember { mutableStateOf(0) }
        val (selectedAnswer, setSelectedAnswer) = remember { mutableStateOf("") }
        var optionsList: MutableList<String> = arrayListOf()
        var isLifeDeducted by remember { mutableStateOf(false) }

        Scaffold() { it ->

            Column(modifier = Modifier.padding(it)
                .fillMaxSize(1f)) {
                Text(text = timer.div(1000).toString())
                Text(text = "Remaining Lives $remainingLives")
                Text(text = "Total Score $totalScore")
                if (remainingLives > 0) {
                    LazyColumn {
                        if (mainViewModel.questionList.size > activeIndex) {
                            val activeQuestion = mainViewModel.questionList[activeIndex]
                            activeQuestion.incorrectAnswers?.let { it ->
                                optionsList = it.toMutableList()
                            }
                            activeQuestion.correctAnswer?.let { it ->
                                optionsList.add(it)
                                optionsList = optionsList.sorted().toMutableList()
                            }

                            item {
                                LaunchedEffect(
                                    key1 = mainViewModel.questionList[activeIndex],
                                    block = {
                                        while (timer.div(1000) > 0) {
                                            delay(1000)
                                            timer -= 1000
                                        }
                                        // if question is still visible to user just deduct his life
                                        remainingLives -= 1
                                        isLifeDeducted = true
                                    })
                                Spacer(modifier = Modifier.height(30.dp))
                                Text(text = mainViewModel.questionList[activeIndex].question.toString())
                            }

                            items(1) {
                                OptionRadioButtonComponent(
                                    optionsList,
                                    selectedAnswer,
                                    setSelectedAnswer
                                )
                            }
                        }else{
                            item {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    Button(onClick = {
                        var achievedScore = 0
                        val currentQuestion = mainViewModel.questionList[activeIndex]
                        if (currentQuestion.correctAnswer == selectedAnswer) {
                            currentQuestion.difficulty?.let { itt ->
                                achievedScore = Point.valueOf(itt.uppercase()).value
                                totalScore += achievedScore
                            }
                        } else if(!isLifeDeducted){
                            // checked if user life has not been deducted yet as we want to deduct user life for a
                            // question only once either due to late answer or due to wrong answer
                            // if he has answered late his life has been detected at just completion of timer
                            remainingLives -= 1

                        }
                        // check if user has answered either correctly or incorrectly and save question with zero point
                        // in database that indicates it was answered wrong
                        if (selectedAnswer != "") {
                            CoroutineScope(Dispatchers.IO).launch {
                                currentQuestion.achievedScore = achievedScore
                                mainViewModel.insertQuestion(currentQuestion)
                            }
                        }

                        timer = 5000 // reset timer
                        setSelectedAnswer("") // reset selectedAnswer
                        isLifeDeducted = false // reset life deduction flag
                        if (activeIndex < mainViewModel.questionList.size - 1) {
                            activeIndex += 1
                        } else {
                            mainViewModel.questionList.clear()
                            mainViewModel.getQuestionsByFilter() // recall next questions
                            activeIndex = 0
                        }
                    }) {
                        Text(text = "Next")
                    }
                } else {
                    Text(text = "You Lost")
                }
            }
        }
    }

    @Composable
    fun NormalQuiz() {
        var activeIndex by remember { mutableStateOf(0) }
        var totalScore by remember { mutableStateOf(0) }
        val (selectedAnswer, setSelectedAnswer) = remember { mutableStateOf("") }
        val isOpenDialog = remember { mutableStateOf(false)  }
        val isSessionCompleted = remember { mutableStateOf(false)  }

        Column(modifier = Modifier
            .fillMaxSize(1f)
            .padding(all = 10.dp)) {
            var optionsList: MutableList<String> = arrayListOf()
            Text(text = "Total Score $totalScore")
            if(!isSessionCompleted.value){LazyColumn {
                if (mainViewModel.questionList.size > activeIndex) {
                    val activeQuestion = mainViewModel.questionList[activeIndex]
                    activeQuestion.incorrectAnswers?.let {
                        optionsList = it.toMutableList()
                        optionsList = optionsList.sorted().toMutableList()
                    }
                    activeQuestion.correctAnswer?.let {
                        optionsList.add(it)
                        optionsList = optionsList.sorted().toMutableList()
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(text = mainViewModel.questionList[activeIndex].question.toString())
                    }
                    items(1) {
                        OptionRadioButtonComponent(optionsList, selectedAnswer, setSelectedAnswer)
                    }
                } else {
                    item {
                        CircularProgressIndicator()
                    }
                }
            }
                Button(onClick = {
                    var achievedScore = 0
                    val currentQuestion = mainViewModel.questionList[activeIndex]
                    if (currentQuestion.correctAnswer == selectedAnswer) {
                        currentQuestion.difficulty?.let { difficulty ->
                            achievedScore = Point.valueOf(difficulty.uppercase()).value
                            totalScore += achievedScore
                        }
                    }
                    if (selectedAnswer != "") {
                        CoroutineScope(Dispatchers.IO).launch {
                            currentQuestion.achievedScore = achievedScore
                            mainViewModel.insertQuestion(currentQuestion)
                        }
                    }
                    setSelectedAnswer("")
                    if (activeIndex < mainViewModel.questionList.size - 1) {
                        activeIndex += 1
                    } else {
                        isOpenDialog.value = true
                        isSessionCompleted.value = true
                    }
                }) {
                    Text(text = "Next")
                }
            }else{
                Button(onClick = {finish()
                }) {
                    Text(text = "Go Back")
                }
                }

        }


        if (isOpenDialog.value) {

            AlertDialog(
                onDismissRequest = {
                    isOpenDialog.value = false
                },
                title = {
                    Text(text = "Session Completed!")
                },
                text = {
                    Text("Your Score is $totalScore")
                },
                confirmButton = {
                    Button(

                        onClick = {
                            isOpenDialog.value = false
                        }) {
                        Text("Done")
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OptionRadioButtonComponent(
        list: MutableList<String>,
        selected: String,
        setSelected: (selected: String) -> Unit,
    ) {

        Column {
            list.forEach { text ->
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (text == selected),
                            onClick = { setSelected(text) }
                        )
                        .padding(horizontal = 0.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selected),
                        onClick = { setSelected(text) }
                    )
                    Text(
                        text = text,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }

}