package com.newsapp.domain.usecases

import com.newsapp.data.remote.NetworkResult
import com.newsapp.data.remote.safeApiCall
import com.newsapp.domain.model.Article
import com.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchArticleUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(query: String): Flow<NetworkResult<List<Article>>> {
        return repository.searchArticles(query)
    }
}