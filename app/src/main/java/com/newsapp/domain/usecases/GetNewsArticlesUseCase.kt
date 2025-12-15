package com.newsapp.domain.usecases

import androidx.paging.PagingData
import com.newsapp.domain.model.Article
import com.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(query: String = "technology"): Flow<PagingData<Article>> {
        return repository.getNewsArticles(query)
    }
}