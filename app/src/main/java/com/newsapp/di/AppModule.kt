package com.newsapp.di

import com.newsapp.data.mapper.ArticleMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideArticleMapper(): ArticleMapper {
        return ArticleMapper()
    }
}
