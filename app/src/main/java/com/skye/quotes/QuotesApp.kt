package com.skye.quotes

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skye.quotes.network.Quote
import com.skye.quotes.presentation.screens.homeScreen.HomeScreen
import com.skye.quotes.presentation.screens.homeScreen.QuoteUiState
import com.skye.quotes.presentation.screens.homeScreen.QuoteViewModel
import com.skye.quotes.ui.theme.QuotesTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val quoteViewModel: QuoteViewModel = viewModel(factory = QuoteViewModel.Factory)
    Scaffold(
        topBar = {
            QuotesTopAppBar(
                todayQuoteUiState = quoteViewModel.todayQuoteUiState,
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        HomeScreen(
            randomQuoteUiState = quoteViewModel.randomQuoteUiState,
            onRefresh = quoteViewModel::reloadQuote
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuotesTopAppBar(
    todayQuoteUiState: QuoteUiState,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {

    var showTodayQuote by remember { mutableStateOf(false) }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(
                onClick = {
                    showTodayQuote = !showTodayQuote
                }
            ) {
                Icon(
                    imageVector = if (showTodayQuote) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (showTodayQuote) "Hide today's quote" else "Show today's quote"
                )
            }
        },
        title = {
            if (showTodayQuote) {
                when (todayQuoteUiState) {
                    is QuoteUiState.Success -> {
                        Column {
                            Text(
                                text = "Quote of the Day",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = todayQuoteUiState.quote.quote,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    is QuoteUiState.Loading -> {
                        ContainedLoadingIndicator()
                    }
                    is QuoteUiState.Error -> {
                        Text(
                            text = todayQuoteUiState.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                Text(
                    text = "Quotes",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun QuotesTopAppBarSuccessPreview() {
    QuotesTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        QuotesTopAppBar(
            todayQuoteUiState = QuoteUiState.Success(
                quote = Quote(
                    quote = "The only way to do great work is to love what you do.",
                    author = "Steve Jobs",
                    html = ""
                )
            ),
            scrollBehavior = scrollBehavior
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun QuotesTopAppBarLoadingPreview() {
    QuotesTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        QuotesTopAppBar(
            todayQuoteUiState = QuoteUiState.Loading,
            scrollBehavior = scrollBehavior
        )
    }
}