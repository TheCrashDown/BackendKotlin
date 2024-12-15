package services

import models.*
import kotlin.math.*


// в дз 5 это будет заменено на обращения в базу, пока все в оперативке
class PostService {

    private val posts = mutableListOf<Post>()

    fun createPost(data: PostCreateModel, authorId: String): Post {
        val post = Post(content = data.content, authorId = authorId)
        posts.add(post)
        return post
    }

    fun getPostById(id: String): Post? {
        return posts.find { it.id == id }
    }

    fun getAllPosts(limit: Int, offset: Int): List<Post> {
        val from = offset
        val to = min(from + limit, posts.size)
        println(posts)
        println(from)
        println(to)
        return posts.subList(from, to)
    }

    fun updatePost(id: String, updatedPost: Post): Post? {
        val post = posts.find { it.id == id } ?: return null

        post.updateContent(newContent = updatedPost.content)

        return post
    }

    fun deletePost(id: String): Post? {
        val post = posts.find { it.id == id } ?: return null
        posts.remove(post)
        return post
    }
}
