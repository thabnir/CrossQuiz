import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*

@Composable
@Preview
fun App() {

    val quizzer = Quizzer()
    MaterialTheme(
        if (isSystemInDarkTheme()) {
            darkColors()
        } else {
            lightColors()
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
        ) {
            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()
            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxHeight().fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "Quizzer",
                        style = MaterialTheme.typography.h1,
                        color = MaterialTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                    )
                }
                itemsIndexed(quizzer.questions) { index, question ->
                    QuestionPanel(
                        question, index, coroutineScope, listState
                    ).Display()
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Quizzer",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center), size = DpSize(1000.dp, 1300.dp)
        ),
    ) {
        App()
    }
}
