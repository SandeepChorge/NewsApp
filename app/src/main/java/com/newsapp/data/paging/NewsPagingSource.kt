package com.newsapp.data.paging


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.newsapp.BuildConfig
import com.newsapp.data.local.dao.ArticleDao
import com.newsapp.data.mapper.ArticleMapper
import com.newsapp.data.remote.api.NewsApiService
import com.newsapp.domain.model.Article
import retrofit2.HttpException
import java.io.IOException

/**
 * PagingSource for loading articles with pagination
 * Handles loading pages of articles from the API
 */
class NewsPagingSource(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val mapper: ArticleMapper,
    private val query: String
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1

        return try {
            // Fetch from API
            val response = apiService.getEverything(
                query = query,
                page = page,
                pageSize = params.loadSize,
                apiKey = BuildConfig.NEWS_API_KEY
            )

            // Get bookmarked URLs to maintain bookmark state
            val bookmarkedUrls = getBookmarkedUrls()

            // Map to domain models and preserve bookmark status
            val articles = response.articles.map { dto ->
                val isBookmarked = bookmarkedUrls.contains(dto.url)
                mapper.toDomain(dto, isBookmarked)
            }

            // Cache articles in database
            val entities = articles.map { mapper.toEntity(it) }//response.articles.map { mapper.toEntity(it) }
            articleDao.insertArticles(entities)

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            e.printStackTrace()
            // Network error
            LoadResult.Error(e)
        } catch (e: HttpException) {
            e.printStackTrace()
            // HTTP error
            LoadResult.Error(e)
        } catch (e: Exception) {
            // Other errors
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    /**
     * Get set of bookmarked article URLs from database
     */
    private suspend fun getBookmarkedUrls(): Set<String> {
        return try {
            // This is a simplified version - in production, you'd want to use Flow
            // For now, we'll just check against cached articles in DB
            emptySet() // TODO: Implement proper bookmark checking
        } catch (e: Exception) {
            emptySet()
        }
    }
}