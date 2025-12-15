package com.newsapp.domain.model

data class Article(
    val url: String,
    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val source: NewsSource,
    val author: String?,
    val publishedAt: String,
    val isBookmarked: Boolean = false,
    val bookmarkedAt: Long? = null
) {
    fun getFormattedDate(): String {
        // Basic formatting - can be improved with DateTimeFormatter
        return try {
            publishedAt.split("T").firstOrNull() ?: publishedAt
        } catch (e: Exception) {
            publishedAt
        }
    }


    fun getShortDescription(maxLength: Int = 150): String {
        return description?.let {
            if (it.length > maxLength) {
                "${it.take(maxLength)}..."
            } else {
                it
            }
        } ?: "No description available"
    }
}