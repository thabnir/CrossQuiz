import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class QuestionPanel(
    val question: Question,
    val index: Int,
    val cs: CoroutineScope,
    val state: LazyListState,
) {
    val dateString = when (question) {
        is CrosswordQuestion -> question.formattedDate
        is DictionaryQuestion -> null
        else -> null
    }
    val correctColor = Color(50, 200, 50)
    val incorrectColor = Color(200, 50, 50)

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Display() {
        // TODO: make it autocapitalize only on questions that need it

        var inputText by rememberSaveable { mutableStateOf("") }
        var isAnswered by rememberSaveable { mutableStateOf(false) }
        var isCorrect by rememberSaveable { mutableStateOf(false) }
        Card(
            elevation = 5.dp, modifier = Modifier.fillMaxHeight().fillMaxWidth(.5f), border = BorderStroke(
                2.dp, if (isAnswered) if (isCorrect) correctColor else incorrectColor else colors.onBackground
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxHeight().fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                )
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { if (it.length <= question.answer.length) inputText = it.uppercase() },
                    label = { Text("Answer has ${question.answer.length} letters") },
                    singleLine = true,
                    isError = isAnswered && !isCorrect,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = colors.background,
                        textColor = colors.onSurface,
                        focusedBorderColor = if (isAnswered && isCorrect) correctColor else colors.primary,
                        unfocusedBorderColor = if (isAnswered && isCorrect) correctColor else colors.onSurface,
                        errorLabelColor = incorrectColor,
                        errorCursorColor = incorrectColor,
                        errorLeadingIconColor = incorrectColor,
                        focusedLabelColor = if (isAnswered && isCorrect) correctColor else colors.onSurface,
                        unfocusedLabelColor = if (isAnswered && isCorrect) correctColor else colors.onSurface,
                    ),
                    modifier = Modifier.onKeyEvent { event ->
                        if (event.key == Key.Enter) {
                            isAnswered = true
                            isCorrect = inputText.equals(question.answer, ignoreCase = true)
                            cs.launch { state.animateScrollToItem(index = index + 1) }
                        }
                        false
                    },
                    readOnly = isAnswered,
                    keyboardOptions = KeyboardOptions(
                        capitalization = if (question is CrosswordQuestion) { // this only works on Android, not on desktop
                            KeyboardCapitalization.Characters
                        } else {
                            KeyboardCapitalization.Words
                        }, imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        isAnswered = true
                        isCorrect = inputText.equals(question.answer, ignoreCase = true)
                    })
                )
                if (isAnswered) {
                    Text(
                        buildAnnotatedString {
                            append("The answer is ")
                            withStyle(
                                style = SpanStyle(
                                    if (isCorrect) correctColor else incorrectColor, fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(question.answer)
                            }
                        },
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
                if (dateString != null) Text(dateString, style = MaterialTheme.typography.caption)
            }
        }
    }
}