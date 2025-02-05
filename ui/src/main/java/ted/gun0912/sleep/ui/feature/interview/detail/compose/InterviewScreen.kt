package ted.gun0912.sleep.ui.feature.interview.detail.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ted.gun0912.sleep.ui.R
import ted.gun0912.sleep.ui.compose.theme.stronglyDeemphasizedAlpha
import ted.gun0912.sleep.ui.compose.util.supportWideScreen
import ted.gun0912.sleep.ui.feature.interview.detail.InterviewScreenData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewScreen(
    screenData: InterviewScreenData,
    isNextEnabled: Boolean,
    onClosePressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Surface(modifier = Modifier.supportWideScreen()) {
        Scaffold(
            topBar = {
                SurveyTopAppBar(
                    onClosePressed = onClosePressed,
                )
            },
            bottomBar = {
                InterviewBottomBar(
                    shouldShowPreviousButton = screenData.shouldShowPreviousButton,
                    shouldShowDoneButton = screenData.shouldShowDoneButton,
                    shouldShowSendButton = !screenData.isChatInterviewDone,
                    isNextButtonEnabled = isNextEnabled,
                    onPreviousPressed = onPreviousPressed,
                    onNextPressed = onNextPressed,
                    onDonePressed = onDonePressed
                )
            },
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewResultScreen(
    onDonePressed: () -> Unit,
) {

    Surface(modifier = Modifier.supportWideScreen()) {
        Scaffold(
            content = { innerPadding ->
                val modifier = Modifier.padding(innerPadding)
                InterviewResult(
                    title = stringResource(R.string.survey_result_title),
                    subtitle = stringResource(R.string.survey_result_subtitle),
                    description = stringResource(R.string.survey_result_description),
                    modifier = modifier
                )
            },
            bottomBar = {
                OutlinedButton(
                    onClick = onDonePressed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            }
        )
    }
}

@Composable
private fun InterviewResult(
    title: String,
    subtitle: String,
    description: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(44.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(20.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // CenterAlignedTopAppBar is experimental in m3
@Composable
fun SurveyTopAppBar(
    onClosePressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        CenterAlignedTopAppBar(
            title = {
                    // TODO
            },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = MaterialTheme.colorScheme.onSurface.copy(stronglyDeemphasizedAlpha)
                    )
                }
            }
        )
    }
}

@Composable
fun InterviewBottomBar(
    shouldShowSendButton: Boolean,
    shouldShowPreviousButton: Boolean,
    shouldShowDoneButton: Boolean,
    isNextButtonEnabled: Boolean,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit
) {
    Surface(shadowElevation = 7.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.systemBars.only(Horizontal + Bottom))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (shouldShowPreviousButton) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onPreviousPressed
                ) {
                    Text(text = stringResource(id = R.string.previous))
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            if (shouldShowSendButton) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onNextPressed,
                    enabled = isNextButtonEnabled,
                ) {
                    Text(text = "전송하기")
                }
            } else {
                if (shouldShowDoneButton) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        onClick = onDonePressed,
                        enabled = isNextButtonEnabled,
                    ) {
                        Text(text = stringResource(id = R.string.done))
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        onClick = onNextPressed,
                        enabled = isNextButtonEnabled,
                    ) {
                        Text(text = stringResource(id = R.string.next))
                    }
                }
            }
        }
    }
}