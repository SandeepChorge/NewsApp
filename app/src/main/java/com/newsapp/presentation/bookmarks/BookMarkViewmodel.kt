package com.newsapp.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsapp.domain.model.Article
import com.newsapp.domain.usecases.BookmarkArticleUseCase
import com.newsapp.domain.usecases.GetBookmarkedArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookMarkViewmodel @Inject constructor(
    getBookmarkedArticlesUseCase: GetBookmarkedArticlesUseCase,
    private val bookmarkArticlesUseCase: BookmarkArticleUseCase
) : ViewModel() {

    val bookmarkedArticles: StateFlow<List<Article>> = getBookmarkedArticlesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeBookmark(article: Article) {
        viewModelScope.launch {
            bookmarkArticlesUseCase(
                article = article,
                bookmark = false
            )
        }
    }
}