package ted.gun0912.sleep.ui.feature.interview.detail.compose.question

import android.app.TimePickerDialog
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ted.gun0912.sleep.ui.compose.theme.slightlyDeemphasizedAlpha
import ted.gun0912.sleep.ui.feature.interview.detail.compose.QuestionWrapper
import java.util.Calendar

@Composable
fun TimeQuestion(
    title: String,
    value: String,
    @StringRes directionsResourceId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        title = title,
        directionsResourceId = directionsResourceId,
        modifier = modifier,
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
                    .copy(alpha = slightlyDeemphasizedAlpha),
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .height(54.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
        ) {
            Text(
                text = value,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.8f)
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f)
            )
        }
    }
}

// todo add preview

