package com.newsapp.presentation.bookmarks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.newsapp.presentation.common.components.ArticleCard
import com.newsapp.presentation.common.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    onArticleClick: (String) -> Unit,
    viewModel: BookMarkViewmodel = hiltViewModel()
) {
    val bookMarkedArticles by viewModel.bookmarkedArticles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            if (bookMarkedArticles.isEmpty()) {
                EmptyState(
                    message = "No bookmarks yet",
                    icon = Icons.Default.BookmarkBorder
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = bookMarkedArticles,
                        key = { it.url }
                    ) { article ->
                        ArticleCard(
                            article = article,
                            onArticleClick = { onArticleClick(article.url) },
                            onBookmarkClick = { viewModel.removeBookmark(article) }
                        )
                    }
                }
            }
        }
    }
}