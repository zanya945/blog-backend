package com.example.article

import Comment
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
