package com.syntxr.korediary.presentation.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable // pastikan pakai ini untuk menjadi komponen compose
fun CustomRadioButton(
    text: String, // text akan ditampilkan sebagai menu custom radio button
    selected: Boolean, // menggunakan boolean untuk memberi kondisi apakah di pilih?
    onSelect: () -> Unit, // perubahan pada kondisi select
    modifier: Modifier = Modifier // untuk mengatur komponen compose
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically // alignment child berada di tengah secara vertikal
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors( // pewarnaan
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium) // ukuran text
    }
}

@Preview // kalau ingin melihat preview
@Composable
fun Preview(){
    CustomRadioButton(text = "AA", selected = true, onSelect = { })
}