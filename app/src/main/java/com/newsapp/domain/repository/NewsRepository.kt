package com.newsapp.domain.repository

import androidx.paging.PagingData
import com.newsapp.data.remote.NetworkResult
import com.newsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsArticles(query : String = "technology"): Flow<PagingData<Article>>
    suspend fun bookmarkArticle(article: Article)
    fun searchArticles(query: String): Flow<NetworkResult<List<Article>>>
    suspend fun getArticleByUrl(url: String): Article?
    suspend fun removeBookmark(url: String)
    fun getBookmarkedArticles(): Flow<List<Article>>
    suspend fun isArticleBookmarked(url: String): Boolean
    suspend fun clearOldCache()
}