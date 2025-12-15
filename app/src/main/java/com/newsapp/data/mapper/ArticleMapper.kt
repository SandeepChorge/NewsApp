package com.newsapp.data.mapper

import com.newsapp.data.local.entity.ArticleEntity
import com.newsapp.data.remote.dto.ArticleDto
import com.newsapp.domain.model.Article
import com.newsapp.domain.model.NewsSource

class ArticleMapper {

    fun toDomain(dto: ArticleDto, isBookmarked: Boolean = false): Article {
        return Article(
            url = dto.url,
            title = dto.title,
            description = dto.description,
            imageUrl = dto.urlToImage,
            source = NewsSource(
                id = dto.source.id,
                name = dto.source.name
            ),
            publishedAt = dto.publishedAt,
            isBookmarked = isBookmarked,
            content = dto.content,
            author = dto.author
        )
    }

    fun toDomain(entity: ArticleEntity): Article {
        return Article(
            url = entity.url,
            title = entity.title,
            description = entity.description,
            content = entity.content,
            imageUrl = entity.imageUrl,
            source = NewsSource(
                id = entity.sourceId,
                name = entity.sourceName
            ),
            author = entity.author,
            publishedAt = entity.publishedAt,
            isBookmarked = entity.isBookmarked,
            bookmarkedAt = entity.bookmarkedAt
        )
    }

    fun toEntity(article: Article): ArticleEntity {
        return ArticleEntity(
            url = article.url,
            title = article.title,
            description = article.description,
            content = article.content,
            imageUrl = article.imageUrl,
            sourceName = article.source.name,
            sourceId = article.source.id,
            author = article.author,
            publishedAt = article.publishedAt,
            isBookmarked = article.isBookmarked,
            bookmarkedAt = article.bookmarkedAt,
            cachedAt = System.currentTimeMillis()
        )
    }

    fun toDomainList(entities: List<ArticleEntity>): List<Article> {
        return entities.map { toDomain(it) }
    }

}