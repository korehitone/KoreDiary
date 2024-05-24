package com.syntxr.korediary.presentation.detail

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.canopas.editor.ui.data.RichEditorState
import com.canopas.editor.ui.ui.RichEditor
import com.example.texteditor.parser.JsonEditorParser
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import kotlinx.coroutines.launch

data class DetailScreenNavArgs(
    // agar bisa menerima kiriman data
    val title: String,
    val ysiyg: String,
    val mood: String,
    val date: String,
)

@Destination(navArgsDelegate = DetailScreenNavArgs::class)
@Composable
fun DetailScreen(
    navigator: DestinationsNavigator,
    viewModel: DetaiViewModel = hiltViewModel(),
) {
    val newState = remember { // state untuk rich editor, berbasis JSON
        val input = viewModel.valueHandle
        RichEditorState.Builder()
            .setInput(input)
            .adapter(JsonEditorParser())
            .build()
    }

    val scope = rememberCoroutineScope()
    val screenshotState = rememberScreenshotState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navigator.navigateUp() }) {
                Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null)
            }

            IconButton(
                onClick = {
                    if (screenshotState.imageBitmap != null) {
                        scope.launch {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "image/*"
                            intent.putExtra(
                                Intent.EXTRA_STREAM,
                                screenshotState.imageBitmap?.let {
                                    viewModel.shareImage(
                                        context,
                                        it.asAndroidBitmap()
                                    )
                                }
                            )

                            context.startActivity(
                                Intent.createChooser(intent, "Share With")
                            )
                        }
                    }else{
                        Toast.makeText(context, "click on screen first to capture", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(imageVector = Icons.Rounded.ArrowOutward, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ScreenshotBox(
            screenshotState = screenshotState,
            modifier = Modifier
                .clickable {
                    scope.launch {
                        screenshotState.capture()
                    }
                }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = viewModel.titleHandle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(text = viewModel.moodHandle)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = viewModel.dateHandle)
                Spacer(modifier = Modifier.height(8.dp))

                RichEditor(
                    state = newState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, Color.Gray)
                        .padding(5.dp)
                        .background(Color.White)
                )
            }
        }
    }

}