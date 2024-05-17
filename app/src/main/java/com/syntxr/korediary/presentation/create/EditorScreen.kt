package com.syntxr.korediary.presentation.create

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.canopas.editor.ui.data.RichEditorState
import com.canopas.editor.ui.ui.RichEditor
import com.example.texteditor.parser.JsonEditorParser
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.syntxr.korediary.data.kotpref.LocalUser
import com.syntxr.korediary.data.source.remote.serializable.PostDto
import com.syntxr.korediary.presentation.create.component.StyleContainer
import com.syntxr.korediary.presentation.destinations.EmojiPickerBottomSheetDestination
import java.util.UUID

data class EditorScreenNavArgs( // agar bisa menerima kiriman data
    val isEdit: Boolean,
    val uuid: String,
    val title: String,
    val ysiyg: String,
    val mood: String,
)

@Destination(
    navArgsDelegate = EditorScreenNavArgs::class
)
@Composable
fun EditorScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<EmojiPickerBottomSheetDestination, String>,
    viewModel: EditorViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    var titleState by remember {
        mutableStateOf(viewModel.titleHandle)
    }

    var selectedEmoji by remember {
        mutableStateOf(viewModel.moodHandle)
    }

    var showSaveDialog by remember { mutableStateOf(false) }

    resultRecipient.onNavResult(listener = { navResult -> // menerima nilai yang dikirim dari bottom sheet
        when (navResult) {
            NavResult.Canceled -> {} /*Toast.makeText(context, "Bottom sheet canceled", Toast.LENGTH_SHORT).show()*/
            is NavResult.Value -> {
                selectedEmoji = navResult.value
            }
        }
    })

    val newState = remember { // state untuk rich editor, berbasis JSON
        val input = viewModel.valueHandle
        RichEditorState.Builder()
            .setInput(input)
            .adapter(JsonEditorParser())
            .build()
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = it.calculateBottomPadding())
                .padding(top = it.calculateTopPadding()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            IconButton(
                onClick = { navigator.navigateUp() /* kembali ke halaman sebelumnya */ },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = titleState,
                    onValueChange = { value ->
                        titleState = value
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    ),
                    label = {
                        Text(text = "Title Here")
                    },
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = { navigator.navigate(EmojiPickerBottomSheetDestination) }) {
                    Text(text = selectedEmoji)
                }
            }

            StyleContainer(
                state = newState,
                onSave = {
                    showSaveDialog = true
                }
            )
            RichEditor(
                state = newState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color.Gray)
                    .padding(5.dp)
                    .background(Color.White)

            )


            AnimatedVisibility(visible = showSaveDialog) {
                SaveDialog(
                    onDismissRequest = { showSaveDialog = false },
                    onConfirmation = {
                        val postDto = PostDto(
                            createdAt = "now()",
                            uuid = "${LocalUser.username}-${UUID.randomUUID() /* memakai uuid agar safe, karena uuid tidak mudah ditebak*/}",
                            userId = LocalUser.uuid,
                            title = titleState,
                            value = newState.output(),
                            mood = selectedEmoji
                        )
                        if (titleState.isNotEmpty() && newState.output().isNotEmpty() && selectedEmoji.isNotEmpty()){
                            if (viewModel.isEditorHandle) {
                                viewModel.updateCloud(titleState, newState.output(), selectedEmoji)
                            } else {
                                viewModel.saveCloud(postDto)
                            }
                            showSaveDialog = false
                            navigator.navigateUp()
                        }else{
                            Toast.makeText(context, "These blank `(*>﹏<*)′", Toast.LENGTH_SHORT).show()
                            showSaveDialog = false
                        }
                    }
                )
            }

        }
    }
}

@Composable
fun SaveDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    text : String  = "do you want to do this action?"
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(text = "Sure")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Eh, cancel")
            }
        },
        text = {
            Text(text = text)
        }
    )
}

//@Preview
//@Composable
//fun EditorPrev() {
//    EditorScreen()
//}