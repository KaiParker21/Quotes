package com.skye.quotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skye.quotes.network.Quote
import com.skye.quotes.presentation.screens.homeScreen.HomeScreen
import com.skye.quotes.presentation.screens.homeScreen.QuoteUiState
import com.skye.quotes.presentation.screens.homeScreen.QuoteViewModel
import com.skye.quotes.ui.theme.QuotesTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val quoteViewModel: QuoteViewModel = viewModel(factory = QuoteViewModel.Factory)
    var showTodayQuote by rememberSaveable { mutableStateOf(false) }
    val hasError =
        quoteViewModel.randomQuoteUiState is QuoteUiState.Error || 
                quoteViewModel.randomQuoteUiState is QuoteUiState.RateLimited

    Scaffold(
        floatingActionButton = {
            val isRateLimited = quoteViewModel.randomQuoteUiState is QuoteUiState.RateLimited
            LargeFloatingActionButton(
                onClick = {
                    if (!isRateLimited) { quoteViewModel.reloadQuote() }
                },
                containerColor =
                    if (isRateLimited)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primary,

                contentColor =
                    if (isRateLimited)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onPrimary

            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload quote"
                )
            }

        },
        topBar = {
            QuotesTopAppBar(
                showTodayQuote = showTodayQuote,
                hasError = hasError,
                onToggleTodayQuote = { showTodayQuote = !showTodayQuote },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = showTodayQuote
            ) {
                TodayQuoteSection(
                    todayQuoteUiState = quoteViewModel.todayQuoteUiState,
                    hasGlobalError = hasError
                )
            }
            HomeScreen(
                randomQuoteUiState = quoteViewModel.randomQuoteUiState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesTopAppBar(
    showTodayQuote: Boolean,
    hasError: Boolean,
    onToggleTodayQuote: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {

    val colors =
        if (hasError) {
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                scrolledContainerColor = MaterialTheme.colorScheme.errorContainer,
                titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        } else {
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }


    LargeTopAppBar(
        title = {
            Text(
                text = "Quotes",
                style = MaterialTheme.typography.headlineLarge
            )
        },
        actions = {
            IconButton(onClick = onToggleTodayQuote) {
                Icon(
                    imageVector =
                        if (showTodayQuote)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                    contentDescription = "Toggle today's quote"
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = colors
    )
}



@Composable
fun TodayQuoteSection(
    todayQuoteUiState: QuoteUiState,
    hasGlobalError: Boolean
) {
    val containerColor =
        if (hasGlobalError)
            MaterialTheme.colorScheme.errorContainer
        else
            MaterialTheme.colorScheme.surface

    val baseContentColor =
        if (hasGlobalError)
            MaterialTheme.colorScheme.onErrorContainer
        else
            MaterialTheme.colorScheme.onSurface


    when (todayQuoteUiState) {
        is QuoteUiState.Success -> {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = containerColor
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "Quote of the Day",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (hasGlobalError)
                            baseContentColor.copy(alpha = 0.9f)
                        else
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = todayQuoteUiState.quote.quote,
                        style = MaterialTheme.typography.titleMedium,
                        color = baseContentColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "- ${todayQuoteUiState.quote.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = baseContentColor.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End)
                    )

                }
            }
        }

        is QuoteUiState.Loading -> {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Quote of the Day",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        is QuoteUiState.Error -> {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Unable to load today's quote",
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        else -> {}
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun QuotesTopAppBarPreview_Default() {
    QuotesTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        QuotesTopAppBar(
            showTodayQuote = false,
            onToggleTodayQuote = {},
            scrollBehavior = scrollBehavior,
            hasError = false
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun QuotesTopAppBarPreview_Error() {
    QuotesTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        QuotesTopAppBar(
            showTodayQuote = true,
            onToggleTodayQuote = {},
            scrollBehavior = scrollBehavior,
            hasError = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TodayQuoteSectionPreview_Success() {
    QuotesTheme {
        TodayQuoteSection(
            todayQuoteUiState = QuoteUiState.Success(
                quote = Quote(
                    quote = "The only way to do great work is to love what you do.",
                    author = "Steve Jobs",
                    html = ""
                )
            ),
            hasGlobalError = false
        )
    }
}

