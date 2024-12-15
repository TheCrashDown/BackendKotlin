package routers

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import models.UserRequestModel
import services.UserService

fun Application.authRouter(userService: UserService) {
    routing {
        route("/register") {
            post {
                val user = call.receive<UserRequestModel>()
                val registeredUser = userService.register(user.login, user.password)
                if (registeredUser == null) {
                    call.respond(io.ktor.http.HttpStatusCode.BadRequest, "Login already exists")
                    return@post
                }
                call.respond(io.ktor.http.HttpStatusCode.OK, "Success")
            }
        }
        route("/login") {
            post {
                val loginRequest = call.receive<UserRequestModel>()
                val user = userService.login(loginRequest.login, loginRequest.password)
                if (user == null) {
                    call.respond(io.ktor.http.HttpStatusCode.Unauthorized, "Invalid login or password")
                    return@post
                }

                val token = JWT.create()
                    .withClaim("login", user.login)
                    .withClaim("userId", user.id)
                    .sign(Algorithm.HMAC256("secret"))
                call.respond(mapOf("token" to token))
            }
        }
    }
}
