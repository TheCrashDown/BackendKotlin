package models

import kotlinx.serialization.Serializable
import utils.Util
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Serializable
data class Post(
    val id: String = UUID.randomUUID().toString(),
    var content: String,
//    var authorId: String, // later
    val createdAt: String = Util.getCurrentDate(),
    var updatedAt: String = createdAt
) {

    fun updateContent(newContent: String) {
        content = newContent
        updatedAt = Util.getCurrentDate()
    }
}
