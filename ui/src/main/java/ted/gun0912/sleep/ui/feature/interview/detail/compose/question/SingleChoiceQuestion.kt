package ted.gun0912.sleep.ui.feature.interview.detail.compose.question

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ted.gun0912.sleep.model.AnswerOption
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.feature.interview.detail.compose.QuestionWrapper

@Composable
fun SingleChoiceQuestion(
    title: String,
    @StringRes directionsResourceId: Int,
    possibleAnswers: List<AnswerOption>,
    selectedAnswer: AnswerOption?,
    onOptionSelected: (Int, AnswerOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        title = title,
        directionsResourceId = directionsResourceId,
        modifier = modifier.selectableGroup(),
    ) {
        possibleAnswers.forEachIndexed { index, answerOption ->
            val selected = answerOption == selectedAnswer
            RadioButtonWithImageRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = answerOption.answer,
                selected = selected,
                onOptionSelected = { onOptionSelected(index, answerOption) }
            )
        }
    }
}

@Composable
fun RadioButtonWithImageRow(
    text: String,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected,
                onClick = onOptionSelected,
                role = Role.RadioButton
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                RadioButton(selected, onClick = null)
            }
        }
    }
}

@Preview
@Composable
fun SingleChoiceQuestionPreview() {
    val possibleAnswers = listOf(
        AnswerOption("answer1"),
        AnswerOption("answer2"),
        AnswerOption("answer3"),
    )
    var selectedAnswer by remember { mutableStateOf<AnswerOption?>(null) }

    SingleChoiceQuestion(
        title = "pick one",
        directionsResourceId = R.string.select_one,
        possibleAnswers = possibleAnswers,
        selectedAnswer = selectedAnswer,
        onOptionSelected = { index, option -> selectedAnswer = option },
    )
}

data class Superhero(@StringRes val stringResourceId: Int)