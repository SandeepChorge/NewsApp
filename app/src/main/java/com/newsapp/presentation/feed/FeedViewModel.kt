package com.newsapp.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.newsapp.domain.model.Article
import com.newsapp.domain.usecases.BookmarkArticleUseCase
import com.newsapp.domain.usecases.GetNewsArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getArticlesUseCase : GetNewsArticlesUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("technology")
    val searchQuery = _searchQuery.asStateFlow()

    val articlesFlow : Flow<PagingData<Article>> = searchQuery
        .flatMapLatest {
            query -> getArticlesUseCase(query)
        }.cachedIn(viewModelScope)

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            bookmarkArticleUseCase(article,
                bookmark = !article.isBookmarked)
        }
    }

}