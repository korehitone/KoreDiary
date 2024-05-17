package com.syntxr.korediary.presentation.create.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.FormatUnderlined
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.canopas.editor.ui.data.RichEditorState
import com.canopas.editor.ui.utils.TextSpanStyle


// new try fix

@Composable
fun StyleContainer(
    state: RichEditorState, // memerlukan RichEditorState dari library RichEditor
    onSave : () -> Unit // ketika save data
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Start,
    ) {

        TitleStyleButton(state)
        StyleButton(
            icon = Icons.Rounded.FormatBold,
            style = TextSpanStyle.BoldStyle,
            value = state
        )

        StyleButton(
            icon = Icons.Rounded.FormatItalic,
            style = TextSpanStyle.ItalicStyle,
            value = state,
        )

        StyleButton(
            icon = Icons.Rounded.FormatUnderlined,
            style = TextSpanStyle.UnderlineStyle,
            value = state,
        )

        IconButton(
            modifier = Modifier
                .padding(2.dp)
                .size(48.dp),
            onClick = onSave,
        ) {
            Icon(
                Icons.Rounded.Check, contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

    }
}

@Composable
fun TitleStyleButton(
    value: RichEditorState
) {
    var expanded by remember { mutableStateOf(false) }

    val onItemSelected = { style: TextSpanStyle ->
        value.updateStyle(style)
        expanded = false
    }

    Row {
        IconButton(
            modifier = Modifier
                .padding(2.dp)
                .size(48.dp),
            onClick = { expanded = true },
        ) {
            Row {
                Icon(
                    imageVector = Icons.Rounded.Title, contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize(),
            properties = PopupProperties(false)
        ) {

            DropDownItem(text = "Text",
                isSelected = value.hasStyle(TextSpanStyle.Default),
                onItemSelected = { onItemSelected(TextSpanStyle.Default) })
            DropDownItem(text = "Header 1", isSelected = value.hasStyle(TextSpanStyle.H1Style),
                onItemSelected = { onItemSelected(TextSpanStyle.H1Style) })
            DropDownItem(text = "Header 2", isSelected = value.hasStyle(TextSpanStyle.H2Style),
                onItemSelected = { onItemSelected(TextSpanStyle.H2Style) })
            DropDownItem(text = "Header 3", isSelected = value.hasStyle(TextSpanStyle.H3Style),
                onItemSelected = { onItemSelected(TextSpanStyle.H3Style) })
            DropDownItem(text = "Header 4", isSelected = value.hasStyle(TextSpanStyle.H4Style),
                onItemSelected = { onItemSelected(TextSpanStyle.H4Style) })
            DropDownItem(text = "Header 5", isSelected = value.hasStyle(TextSpanStyle.H5Style),
                onItemSelected = { onItemSelected(TextSpanStyle.H5Style) })
            DropDownItem(text = "Header 6", isSelected = value.hasStyle(TextSpanStyle.H6Style),
                onItemSelected = { onItemSelected(TextSpanStyle.H6Style) })
        }
    }
}

@Composable
fun DropDownItem(
    text: String,
    isSelected: Boolean,
    onItemSelected: () -> Unit
) {

    DropdownMenuItem(
        text = {
            Text(text = text)
        }, onClick = onItemSelected,
        modifier = Modifier.background(
            color = if (isSelected) {
                Color.Gray.copy(alpha = 0.2f)
            } else {
                Color.Transparent
            }, shape = RoundedCornerShape(6.dp)
        )
    )
}

@Composable
fun StyleButton(
    icon: ImageVector,
    style: TextSpanStyle,
    value: RichEditorState,
) {
    IconButton(
        modifier = Modifier
            .padding(2.dp)
            .size(48.dp)
            .background(
                color = if (value.hasStyle(style)) {
                    Color.Gray.copy(alpha = 0.2f)
                } else {
                    Color.Transparent
                }, shape = RoundedCornerShape(6.dp)
            ),
        onClick = {
            value.toggleStyle(style)
        },
    ) {
        Icon(
            imageVector = icon, contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}