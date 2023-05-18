package com.example.triviacompose.app_constants

enum class Point (val difficulty: String, val value: Int){
     EASY( "easy", 1 ),
     MEDIUM( "medium", 2 ),
     HARD( "hard", 3 ),
}
enum class Category (val value: String, val index: Int){
     ANY( "Any Category", 0 ),
     FILM( "Entertainment: Film", 11 ),
     ENTERTAINMENT_MUSIC( "Entertainment: Music", 12 ),
     ENTERTAINMENT_MUSICALS_THEATERS( "Entertainment: Musicals & Theaters", 13 ),
     ENTERTAINMENT_TELEVISION( "Entertainment: Television", 14 ),
     ENTERTAINMENT_VIDEO_GAME( "Entertainment: Video Game", 15 ),
     ENTERTAINMENT_BOARD_GAME( "Entertainment: Board Game", 16 ),
     SCIENCE_AND_NATURE( "Science and Nature", 17 ),
     SCIENCE_COMPUTER( "Science Computer", 18 ),
     SCIENCE_MATHEMATICS( "Science Mathematics", 19 ),
     MYTHOLOGY( "Mythology", 20 ),
}
enum class Type(val type: String, val paramValue: String) {
     ANY("Any Type", ""),
     MULTIPLE_CHOICE("Multiple Choice", "multiple"),
     TRUE_FALSE("True / False", "boolean"),
}
enum class Difficulty(val difficulty: String, val paramValue: String) {
     ANY("Any Difficulty",""),
     EASY("Easy","easy"),
     MEDIUM("Medium","medium"),
     HARD("Hard","hard"),
}