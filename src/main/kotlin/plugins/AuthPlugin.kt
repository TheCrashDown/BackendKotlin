package plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.http.*
import io.ktor.server.response.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm


fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("my-jwt-auth") {
            verifier(
                JWT.require(Algorithm.HMAC256("secret")).build()
            )
            validate { credential ->
                if (credential.payload.getClaim("login").asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is invalid or expired")
            }
        }
    }
}
