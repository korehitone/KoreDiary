package com.syntxr.korediary.presentation.settings.component

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.syntxr.korediary.data.kotpref.GlobalPreferences
import com.syntxr.korediary.data.kotpref.GlobalPreferences.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelect(
    options: List<AppTheme>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.id) },
                    onClick = {
                        expanded = false
                        GlobalPreferences.theme = option
                    }
                )
            }
        }
    }
}