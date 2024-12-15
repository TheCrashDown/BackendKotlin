package routers

import models.*
import services.PostService
import io.ktor.server.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.postRouter(postService: PostService) {
    routing {
        route("/posts") {
            authenticate("my-jwt-auth") {
                put {

                    val tokenInfo = call.principal<JWTPrincipal>()!!
                    val userId = tokenInfo.payload.getClaim("userId").asString()
                    val post = call.receive<PostCreateModel>()

                    val createdPost = postService.createPost(post, userId)

                    call.respond(HttpStatusCode.Created, createdPost)
                }

                get {
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                    val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
                    val posts = postService.getAllPosts(limit, offset)
                    if (posts.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, "Posts not found")
                        return@get
                    }
                    call.respond(posts)
                }
            }
        }


        route("/posts/{id}") {
            authenticate("my-jwt-auth") {
                get {
                    val id = call.parameters["id"]
                    if (id == null) {
                        println("Returning bad request")
                        call.respond(HttpStatusCode.BadRequest, "You must specify a post id")
                        return@get
                    }

                    val post = postService.getPostById(id)
                    println("Found post: $post")
                    if (post != null) {
                        call.respond(HttpStatusCode.OK, post)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Post not found")
                    }
                }

                patch {
                    val tokenInfo = call.principal<JWTPrincipal>()!!
                    val userId = tokenInfo.payload.getClaim("userId").asString()
                    val id = call.parameters["id"]
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "You must specify a post id")
                        return@patch
                    }
                    
                    val request = call.receive<PostCreateModel>()
                    val newPost = Post(content = request.content, authorId = userId)
                    val post = postService.getPostById(id)

                    if (post == null) {
                        call.respond(HttpStatusCode.NotFound, "Post not found")
                        return@patch
                    }

                    if (post.authorId != userId) {
                        call.respond(HttpStatusCode.Forbidden, "You do not have permission modify this post")
                    }

                    val updatedPost = postService.updatePost(id, newPost)

                    if (updatedPost != null) {
                        call.respond(HttpStatusCode.OK, updatedPost)

                    } else {
                        call.respond(HttpStatusCode.NotFound, "Post not found")

                    }
                }

                delete {
                    val tokenInfo = call.principal<JWTPrincipal>()!!
                    val userId = tokenInfo.payload.getClaim("userId").asString()
                    val id = call.parameters["id"]
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "You must specify a post id")
                        return@delete
                    }

                    val post = postService.getPostById(id)
                    if (post == null) {
                        call.respond(HttpStatusCode.NotFound, "Post not found")
                        return@delete
                    }
                    if (post.authorId != userId) {
                        call.respond(HttpStatusCode.Forbidden, "You do not have permission delete this post")
                    }
                    val deletedPost = postService.deletePost(id)
                    if (deletedPost != null) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Post not found")
                    }
                }
            }
        }
    }
}
