package com.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.newsapp.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("Select * from articles where isBookmarked = 1 order by bookmarkedAt desc")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(entities: List<ArticleEntity>)

    @Query("SELECT * FROM articlxes WHERE url = :url")
    suspend fun getArticleByUrl(url: String): ArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(articles: ArticleEntity)

    @Update
    suspend fun updateArticle(article: ArticleEntity)

    @Query("UPDATE articles SET isBookmarked = :isBookmarked, bookmarkedAt = :bookmarkedAt WHERE url = :url")
    suspend fun updateBookmarkStatus(url: String, isBookmarked: Boolean, bookmarkedAt: Long?)

    @Query("DELETE FROM articles WHERE isBookmarked = 0")
    suspend fun clearNonBookmarked()

    @Query("UPDATE articles SET isBookmarked = 0 WHERE url = :url")
    suspend fun removeBookmark(url: String)
}