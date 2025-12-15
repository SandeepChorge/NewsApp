package com.newsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.newsapp.BuildConfig
import com.newsapp.data.local.dao.ArticleDao
import com.newsapp.data.mapper.ArticleMapper
import com.newsapp.data.paging.NewsPagingSource
import com.newsapp.data.remote.NetworkResult
import com.newsapp.data.remote.api.NewsApiService
import com.newsapp.data.remote.safeApiCall
import com.newsapp.domain.model.Article
import com.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val mapper: ArticleMapper
) : NewsRepository {

    companion object {
        private const val PAGE_SIZE = 20
        private const val CACHE_EXPIRY_DAYS = 7L
    }

    override fun getNewsArticles(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                NewsPagingSource(
                    apiService = apiService,
                    articleDao = articleDao,
                    mapper = mapper,
                    query = query
                )
            }
        ).flow
    }

    override suspend fun bookmarkArticle(article: Article) {
        withContext(Dispatchers.IO) {
            val entity = mapper.toEntity(article).copy(
                isBookmarked = true,
                bookmarkedAt = System.currentTimeMillis()
            )
            val existing = articleDao.getArticleByUrl(article.url)
            if (existing != null) {
                articleDao.updateBookmarkStatus(
                    url = article.url,
                    isBookmarked = true,
                    bookmarkedAt = System.currentTimeMillis()
                )
            } else {
                articleDao.insertArticle(entity)
            }
        }
    }

    override fun searchArticles(query: String): Flow<NetworkResult<List<Article>>> = flow {
        emit(NetworkResult.Loading)

        if (query.isBlank()) {
            emit(NetworkResult.Success(emptyList()))
            return@flow
        }
        val result = safeApiCall {
            apiService.searchArticles(
                query = query,
                pageSize = 50,
                apiKey = BuildConfig.NEWS_API_KEY
            )
        }

        when (result) {
            is NetworkResult.Success -> {
                // Get bookmarked URLs to maintain state
                val bookmarkedArticles = articleDao.getArticleByUrl(query)
                val bookmarkedUrls = setOf(bookmarkedArticles?.url ?: "")

                val articles = result.data.articles.map { dto ->
                    val isBookmarked = bookmarkedUrls.contains(dto.url)
                    mapper.toDomain(dto, isBookmarked)
                }
                emit(NetworkResult.Success(articles))
            }

            is NetworkResult.Error -> {
                emit(NetworkResult.Error(result.message, result.code))
            }

            is NetworkResult.Loading -> {
                emit(NetworkResult.Loading)
            }
        }
    }

    override suspend fun getArticleByUrl(url: String): Article? {
        return articleDao.getArticleByUrl(url)?.let { mapper.toDomain(it) }
    }

    override suspend fun removeBookmark(url: String) {
        articleDao.removeBookmark(url)
    }

    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return articleDao.getBookmarkedArticles().map { entities ->
            mapper.toDomainList(entities)
        }
    }

    override suspend fun isArticleBookmarked(url: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun clearOldCache() {
        TODO("Not yet implemented")
    }

}