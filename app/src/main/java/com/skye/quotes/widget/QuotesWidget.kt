package com.skye.quotes.widget


import android.R.attr.maxLines
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.skye.quotes.QuotesApplication

object QuoteWidgetKeys {
    val QUOTE_TEXT = stringPreferencesKey("quote_text")
    val QUOTE_AUTHOR = stringPreferencesKey("quote_author")
    val IS_LOADING = booleanPreferencesKey("is_loading")
}

class QuotesWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()

            val quoteText = prefs[QuoteWidgetKeys.QUOTE_TEXT] ?: "Tap refresh to load a quote"
            val author = prefs[QuoteWidgetKeys.QUOTE_AUTHOR] ?: ""
            val isLoading = prefs[QuoteWidgetKeys.IS_LOADING] ?: false

            GlanceTheme {
                QuotesWidgetContent(
                    quote = quoteText,
                    author = author,
                    isLoading = isLoading
                )
            }
        }
    }
}

class RefreshQuoteAction :
    ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val repository = (context.applicationContext as QuotesApplication).container.quotesRepository

        try {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[QuoteWidgetKeys.IS_LOADING] = true
            }
            QuotesWidget().update(context, glanceId)

            val response = repository.getRandomQuote()
            val randomQuote = response.randomOrNull()

            updateAppWidgetState(context, glanceId) { prefs ->
                randomQuote?.let {
                    prefs[QuoteWidgetKeys.QUOTE_TEXT] = it.quote
                    prefs[QuoteWidgetKeys.QUOTE_AUTHOR] = it.author
                }
                prefs[QuoteWidgetKeys.IS_LOADING] = false
            }
        } catch (e: Exception) {
            updateAppWidgetState(context, glanceId) { it[QuoteWidgetKeys.IS_LOADING] = false }
            Log.e("QuotesWidget", "Error fetching quote", e)
        }

        QuotesWidget().update(context, glanceId)
    }
}

@Composable
fun QuotesWidgetContent(quote: String, author: String, isLoading: Boolean) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .padding(20.dp)
    ) {
        Box(
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isLoading) "Loading wisdom..." else quote,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = GlanceTheme.colors.onBackground,
                    textAlign = TextAlign.Center
                ),
                maxLines = 5
            )
        }
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                text = if (isLoading) "..." else "Refresh",
                onClick = actionRunCallback<RefreshQuoteAction>(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GlanceTheme.colors.primary,
                    contentColor = GlanceTheme.colors.onPrimary
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            if (!isLoading) {
                Text(
                    text = "— $author",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = GlanceTheme.colors.secondary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
