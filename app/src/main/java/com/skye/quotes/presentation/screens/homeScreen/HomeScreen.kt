package com.skye.quotes.presentation.screens.homeScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skye.quotes.network.Quote
import com.skye.quotes.ui.theme.QuotesTheme

@Composable
fun HomeScreen(
    randomQuoteUiState: QuoteUiState,
) {
    when (randomQuoteUiState) {
        is QuoteUiState.Loading ->
            LoadingScreen(
                modifier = Modifier.
                fillMaxSize()
            )
        is QuoteUiState.Error ->
            GenericErrorScreen(
                errorMessage = randomQuoteUiState.error,
                modifier = Modifier.fillMaxSize()
            )
        is QuoteUiState.RateLimited -> RateLimitScreen(
            secondsRemaining = randomQuoteUiState.secondsRemaining
        )
        is QuoteUiState.Success ->
            QuoteScreen(
                randomQuoteUiState.quote,
                modifier = Modifier
                    .padding(24.dp)
            )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularWavyProgressIndicator()
        Spacer(modifier = Modifier.height(196.dp))
    }
}

@Composable
fun RateLimitScreen(
    secondsRemaining: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Too many requests",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Please wait before requesting another quote.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text(
                text = "Try again in $secondsRemaining seconds",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(196.dp))
        }
    }
}


@Composable
fun GenericErrorScreen(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(196.dp))
        }
    }
}

@Composable
fun QuoteScreen(
    quote: Quote,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = quote,
            label = "QuoteTransition"
        ) { targetQuote ->
            QuoteCard(
                quote = targetQuote,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(196.dp))
    }
}



@Composable
fun QuoteCard(
    quote: Quote,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            Text(
                text = quote.quote,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "- ${quote.author}",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuotesScreenPreview() {
    QuotesTheme {
        QuoteScreen(
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
fun RateLimitScreenPreview() {
    QuotesTheme {
        RateLimitScreen(
            secondsRemaining = 10
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GenericErrorScreenPreview() {
    QuotesTheme {
        GenericErrorScreen(
            errorMessage = "Network error",
            modifier = Modifier.fillMaxSize()
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