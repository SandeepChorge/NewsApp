package com.newsapp.domain.usecases

import com.newsapp.domain.model.Article
import com.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class BookmarkArticleUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(article: Article, bookmark: Boolean) {
        if (bookmark) {
            repository.bookmarkArticle(article)
        } else {
            repository.removeBookmark(article.url)
        }
    }
}