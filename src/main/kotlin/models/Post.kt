package models

import kotlinx.serialization.Serializable
import utils.Util
import java.util.*

@Serializable
data class Post(
    val id: String = UUID.randomUUID().toString(),
    var content: String,
    var authorId: String, // later
    val createdAt: String = Util.getCurrentDateTime(),
    var updatedAt: String = createdAt
) {

    fun updateContent(newContent: String) {
        content = newContent
        updatedAt = Util.getCurrentDateTime()
    }
}

@Serializable
data class PostCreateModel(
    var content: String
)
