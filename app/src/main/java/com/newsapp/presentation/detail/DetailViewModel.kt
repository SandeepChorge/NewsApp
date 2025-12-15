package com.newsapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsapp.domain.model.Article
import com.newsapp.domain.usecases.GetArticleByUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val article: Article) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getArticleByUrlUseCase: GetArticleByUrlUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val articleUrl: String = checkNotNull(savedStateHandle["articleUrl"])

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadArticleDetail()
    }

    private fun loadArticleDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val article = getArticleByUrlUseCase(articleUrl)
                if (article != null) {
                    _uiState.value = DetailUiState.Success(article)
                } else {
                    _uiState.value = DetailUiState.Error("Article not found")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(
                    e.localizedMessage ?: "Failed to load article"
                )
            }
        }
    }

    fun retry() {
        loadArticleDetail()
    }
}