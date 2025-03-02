package services

import models.User
import org.mindrot.jbcrypt.BCrypt

// в дз 5 это будет заменено на обращения в базу, пока все в оперативке
class UserService {

    private val users = mutableListOf<User>()

    fun register(login: String, password: String,): User? {
        if (users.find{ it.login == login } != null) {
            return null
        }

        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val user = User(login = login, hashedPassword = hashedPassword)
        users.add(user)
        return user
    }

    fun login(login: String, password: String): User? {
        val user = users.find { it.login == login } ?: return null

        if (BCrypt.checkpw(password, user.hashedPassword)) {
            return user
        }

        return null
    }
}
