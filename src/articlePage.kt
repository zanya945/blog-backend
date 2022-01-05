package com.example.article

import java.util.*

data class articlePage (
    val id: String,
    val tag: String,
    val title: String,
    val like: Int,
    val dislike: Int,
    val date: Date,
    val descripton: List<Comment>,
)

data class Comment(
    val msgid: Int,
    val text: String,
    val messagePerson: String,
    val commentDate: Date,
    val like: Int,
    val dilike: Int,
)