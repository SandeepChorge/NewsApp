package com.newsapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsapp.data.remote.NetworkResult
import com.newsapp.domain.model.Article
import com.newsapp.domain.usecases.BookmarkArticleUseCase
import com.newsapp.domain.usecases.SearchArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class SearchUiState {
    data object Initial : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(val articles: List<Article>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
    data object Empty : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchArticleUseCase: SearchArticleUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchUiState: StateFlow<SearchUiState> = searchQuery
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(SearchUiState.Initial)
            } else {
                searchArticleUseCase(query).map { result ->
                    when (result) {
                        is NetworkResult.Loading -> SearchUiState.Loading
                        is NetworkResult.Success -> {
                            if (result.data.isEmpty()) {
                                SearchUiState.Empty
                            } else {
                                SearchUiState.Success(result.data)
                            }
                        }

                        is NetworkResult.Error -> {
                            SearchUiState.Error(result.message)
                        }
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = SearchUiState.Initial
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            bookmarkArticleUseCase(
                article = article,
                bookmark = !article.isBookmarked
            )
        }
    }
}