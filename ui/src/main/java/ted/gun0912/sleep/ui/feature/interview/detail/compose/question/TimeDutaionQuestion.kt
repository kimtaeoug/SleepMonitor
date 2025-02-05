package ted.gun0912.sleep.ui.feature.interview.detail.compose.question

import androidx.compose.runtime.Composable
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ted.gun0912.sleep.ui.compose.theme.slightlyDeemphasizedAlpha
import ted.gun0912.sleep.ui.feature.interview.detail.compose.QuestionWrapper

@Composable
fun TimeDutaionQuestion(
    title: String,
    @StringRes directionsResourceId: Int,
    hour: String,
    min: String,
    onHourChange: (String) -> Unit,
    onMinChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        title = title,
        directionsResourceId = directionsResourceId,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NumberTextField(
                value = hour,
                onValueChange = onHourChange,
                modifier = Modifier.weight(1f),
                trailingText = "시간"
            )
            NumberTextField(
                value = min,
                onValueChange = onMinChange,
                modifier = Modifier.weight(1f),
                trailingText = "분"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingText: String = ""
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
            .height(54.dp),
        trailingIcon = {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        shape = MaterialTheme.shapes.small,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = slightlyDeemphasizedAlpha),
            containerColor = MaterialTheme.colorScheme.surface
        ),
    )
}

// todo add preview