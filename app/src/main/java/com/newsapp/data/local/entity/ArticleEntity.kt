package com.newsapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val sourceName: String,
    val sourceId: String?,
    val author: String?,
    val publishedAt: String,
    val isBookmarked: Boolean = false,
    val bookmarkedAt: Long? = null,
    val cachedAt: Long = System.currentTimeMillis()
)
