package com.newsapp.presentation.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.newsapp.domain.model.Article
import com.newsapp.presentation.common.components.ArticleCard
import com.newsapp.presentation.common.components.ArticleCardShimmer
import com.newsapp.presentation.common.components.EmptyState
import com.newsapp.presentation.common.components.ErrorState
import com.newsapp.presentation.common.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onArticleClick: (String) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {

    val articles = viewModel.articlesFlow.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Feed") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(modifier = Modifier.fillMaxSize()) {

                items(
                    count = articles.itemCount,
                    key = articles.itemKey { article: Article -> article.url }
                ) { index ->
                    val article = articles[index]
                    if (article != null) {
                        ArticleCard(
                            article = article,
                            onArticleClick = { onArticleClick(article.url) },
                            onBookmarkClick = { viewModel.toggleBookmark(article) }
                        )
                    }
                }
                // shimmer
                if (articles.loadState.refresh is LoadState.Loading) {
                    items(5) {
                        ArticleCardShimmer()
                    }
                }

                if (articles.loadState.append is LoadState.Loading) {
                    item {
                        LoadingIndicator(modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp))
                    }
                }

                if (articles.loadState.refresh is LoadState.Error) {
                    item {
                        ErrorState(
                            message = (articles.loadState.refresh as LoadState.Error)
                                .error.localizedMessage ?: "Failed to load articles",
                            onRetry = { articles.retry() },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                }

                if (articles.loadState.append is LoadState.Error) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Failed to load more articles",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { articles.retry() }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Retry")
                            }
                        }
                    }
                }


            }
            if (articles.loadState.refresh is LoadState.NotLoading && articles.itemCount == 0) {
                EmptyState(
                    message = "No articles available",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}