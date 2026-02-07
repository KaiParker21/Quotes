package com.skye.quotes.presentation.screens.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skye.quotes.network.Quote
import com.skye.quotes.ui.theme.QuotesTheme

@Composable
fun HomeScreen(
    quoteUiState: QuoteUiState,
    onRefresh: () -> Unit
) {
    when (quoteUiState) {
        is QuoteUiState.Loading ->
            LoadingScreen(
                modifier = Modifier.
                fillMaxSize()
            )
        is QuoteUiState.Error ->
            ErrorScreen(
                errorMessage = quoteUiState.error,
                modifier = Modifier.fillMaxSize(),
                onRefresh
            )
        is QuoteUiState.Success ->
            QuoteScreen(
                onRefresh,
                quoteUiState.quote,
                modifier = Modifier
                    .padding(24.dp)
            )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularWavyProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    errorMessage: String,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            modifier = Modifier.padding(16.dp)
        )
        ElevatedButton(
            onClick = onRefresh
        ) {
            Text(text = "Next Quote")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuoteScreen(
    onRefresh: () -> Unit,
    quote: Quote,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        QuoteCard(quote, modifier = modifier)
        ElevatedButton(
            onClick = onRefresh
        ) {
            Text(text = "Next Quote")
        }
    }
}

@Composable
fun QuoteCard(
    quote: Quote,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = quote.quote,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
            )
            Text(
                text = "- ${quote.author}",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .align(Alignment.End)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuotesScreenPreview() {
    QuotesTheme {
        QuoteScreen(
            onRefresh = { },
            quote = Quote(
                quote = "The only way to do great work is to love what you do.",
                author = "Steve Jobs",
                html = ""
            ),
            modifier = Modifier
                .padding(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuoteCardPreview() {
    QuotesTheme {
        QuoteCard(
            quote = Quote(
                quote = "The only way to do great work is to love what you do.",
                author = "Steve Jobs",
                html = ""
            )
        )
    }
}