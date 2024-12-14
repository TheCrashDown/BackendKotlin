package controllers

import models.Post
import db.DataService
import io.ktor.server.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    val dataService = DataService()

    routing {
        route("/posts") {
            put {
                val post = call.receive<Post>()
                println("Received post: $post")
                val createdPost = dataService.createPost(post)
                println("Created post: $createdPost")
                call.respond(HttpStatusCode.Created, createdPost)
            }

            get {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
                val posts = dataService.getAllPosts(limit, offset)
                if (posts.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound, "Posts not found")
                    return@get
                }
                call.respond(posts)
            }
        }


        route("/posts/{id}") {
            get {
                println("Get post by id ${call.parameters["id"]}")
                val id = call.parameters["id"]
                if (id == null) {
                    println("Returning bad request")
                    call.respond(HttpStatusCode.BadRequest, "You must specify a post id")
                    return@get
                }

                val post = dataService.getPostById(id)
                println("Found post: $post")
                if (post != null) {
                    call.respond(HttpStatusCode.OK, post)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Post not found")
                }
            }

            patch {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "You must specify a post id")
                    return@patch
                }

                val updatedPost = call.receive<Post>()
                val post = dataService.updatePost(id, updatedPost)
                if (post != null) {
                    call.respond(HttpStatusCode.OK, post)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Post not found")
                }
            }

            delete {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "You must specify a post id")
                    return@delete
                }

                val post = dataService.deletePost(id)
                if (post != null) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Post not found")
                }
            }
        }
    }
}
