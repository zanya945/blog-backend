import java.util.*

data class Comment(
    val msgid: Int,
    val text: String,
    val messagePerson: String,
    val commentDate: Date,
    val like: Int,
    val dilike: Int,
)