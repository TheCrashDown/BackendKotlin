import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*

import plugins.configureAuthentication
import routers.authRouter
import routers.postRouter
import services.PostService
import services.UserService

fun Application.startup() {
    install(ContentNegotiation) {
        json()
    }
    configureAuthentication()

    val userService = UserService()
    val postService = PostService()

    routing {
        println("qqq")
        postRouter(postService)
        println("www")
        authRouter(userService)
        println("eee")
    }
}

fun main() {
    embeddedServer(Netty, port = 8081, module = Application::startup).start(wait = true)
}