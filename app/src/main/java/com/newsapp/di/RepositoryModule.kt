package com.newsapp.di

import com.newsapp.data.local.dao.ArticleDao
import com.newsapp.data.mapper.ArticleMapper
import com.newsapp.data.remote.api.NewsApiService
import com.newsapp.data.repository.NewsRepositoryImpl
import com.newsapp.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNewsRepository(
        apiService: NewsApiService,
        articleDao: ArticleDao,
        mapper: ArticleMapper
    ): NewsRepository {
        return NewsRepositoryImpl(
            apiService = apiService,
            articleDao = articleDao,
            mapper = mapper
        )
    }
}