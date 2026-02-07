package com.skye.quotes

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skye.quotes.presentation.screens.homeScreen.HomeScreen
import com.skye.quotes.presentation.screens.homeScreen.QuoteViewModel
import com.skye.quotes.ui.theme.QuotesTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = { QuotesTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        val quoteViewModel: QuoteViewModel = viewModel(factory = QuoteViewModel.Factory)
        HomeScreen(
            quoteUiState = quoteViewModel.quoteUiState,
            onRefresh = quoteViewModel::reloadQuote
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuotesTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    LargeFlexibleTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text("Quotes")
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
fun QuotesTopAppBarPreview() {
    QuotesTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        QuotesTopAppBar(scrollBehavior)
    }
}