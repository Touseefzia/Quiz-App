package com.example.triviacompose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.triviacompose.app_constants.ApiPreferences
import com.example.triviacompose.app_constants.Category
import com.example.triviacompose.app_constants.Difficulty
import com.example.triviacompose.app_constants.Type
import com.example.triviacompose.room_db.QuestionDatabase
import com.example.triviacompose.ui.theme.TriviaComposeTheme
import com.example.triviacompose.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var questionDatabase: QuestionDatabase
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            questionDatabase = QuestionDatabase.invoke(this)
            mainViewModel = MainViewModel(questionDatabase = questionDatabase)
            TriviaComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(mainViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(mainViewModel: MainViewModel) {
    val mContext = LocalContext.current
    Scaffold{ padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                mainViewModel.getTillDatePoints().collectLatest {
                    Log.e("MainActivity", it.toString())
                    it?.let { ApiPreferences.totalScoreInDb.value = it.toString() }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 25.dp),
                ) {

                    Text(text = "Your till date score: ${ApiPreferences.totalScoreInDb.value}")
                    SelectionDropdownMenuBox(
                        Category.values(),
                        itemState = ApiPreferences.selectedQuestionCategory,
                        content = { item, itemState, expanded ->
                            DropdownMenuItem(
                                text = { Text(text = item.value) },
                                onClick = {
                                    expanded.value = false
                                    itemState.value = item
                                },
                            )
                        },
                        label = { stateValue -> DropDownLabel(stateValue.value.value) },
                        isExpanded = ApiPreferences.expandedCategory
                    )
                    SelectionDropdownMenuBox(
                        Difficulty.values(),
                        itemState = ApiPreferences.selectedQuestionDifficulty,
                        content = { item, itemState, expanded ->
                            DropdownMenuItem(
                                text = { Text(text = item.difficulty) },
                                onClick = {
                                    expanded.value = false
                                    itemState.value = item
                                },
                            )
                        },
                        label = { stateValue -> DropDownLabel(stateValue.value.difficulty) },
                        isExpanded = ApiPreferences.expandedDifficulty
                    )
                    SelectionDropdownMenuBox(
                        Type.values(),
                        itemState = ApiPreferences.selectedQuestionType,
                        content = { item, itemState, expanded ->
                            DropdownMenuItem(
                                text = { Text(text = item.type) },
                                onClick = {
                                    expanded.value = false
                                    itemState.value = item
                                },
                            )
                        },
                        label = { stateValue -> DropDownLabel(stateValue.value.type) },
                        isExpanded = ApiPreferences.expandedType
                    )
                }
            }
            items(1) {
                Button(
                    modifier = Modifier.height(70.dp),
                    onClick = {
                        ApiPreferences.isQuickMode.value = false
                        mContext.startActivity(Intent(mContext, QuizActivity::class.java))
                    }) {
                    Text(text = "Start Normal Quiz")
                }
                 Button(
                         modifier = Modifier.height(70.dp).padding(top = 15.dp),
                         onClick = {
                             ApiPreferences.isQuickMode.value = true
                             ApiPreferences.selectedQuestionType.value = Type.ANY
                             ApiPreferences.selectedQuestionDifficulty.value = Difficulty.ANY
                             ApiPreferences.selectedQuestionCategory.value = Category.ANY
                             mContext.startActivity(Intent(mContext, QuizActivity::class.java))

                         }) {
                         Text(text = "Quick Mood")
                     }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectionDropdownMenuBox(
    enums: Array<T>,
    itemState: MutableState<T>,
    content: @Composable (item: T, itemState: MutableState<T>, expanded: MutableState<Boolean>) -> Unit,
    label: @Composable (MutableState<T>) -> Unit,
    isExpanded: MutableState<Boolean>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded.value,
            onExpandedChange = {
                isExpanded.value = !isExpanded.value
            }
        ) {
            label.invoke(itemState)
            ExposedDropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = { isExpanded.value = false }
            ) {/*Category.values()*/
                enums.forEach { item ->
                    content.invoke(item = item, itemState = itemState, expanded = isExpanded)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownLabel(
    itemState: String
) {
    TextField(
        value = itemState,
        onValueChange = {},
        readOnly = true,
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = true) },
    )
}