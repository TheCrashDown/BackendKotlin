package models

import kotlinx.serialization.Serializable
import utils.Util
import java.util.*

@Serializable
data class User(
    val id: String = UUID.randomUUID().toString(),
    val login: String,
    val hashedPassword: String,
    val createdAt: String = Util.getCurrentDateTime(),
    var updatedAt: String = createdAt
)

@Serializable
data class UserRequestModel(
    val login: String,
    val password: String,
)
