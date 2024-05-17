package com.syntxr.korediary.presentation.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.syntxr.korediary.utils.DiaryOrder
import com.syntxr.korediary.utils.DiaryOrder.Date
import com.syntxr.korediary.utils.DiaryOrder.Mood
import com.syntxr.korediary.utils.DiaryOrder.Title
import com.syntxr.korediary.utils.OrderBy
import com.syntxr.korediary.utils.OrderBy.Ascending
import com.syntxr.korediary.utils.OrderBy.Descending

@Composable
fun OrderSection(
    onOrderChange: (DiaryOrder, OrderBy) -> Unit,
    modifier: Modifier = Modifier, // kalau kita membuat custom component dan bukan screen, lebih baik menyediakan Modifier seperti ini
    diaryOrder: DiaryOrder = Date,
    order: OrderBy = Descending,
) {
    var selected by remember {
        // menggunakan remember dan mutableState agar kondisi dapat berubah dan tersimpan selama screen dibuka
        mutableStateOf(diaryOrder)
    }

    var ordered by remember {
        mutableStateOf(order)
    }

    Column( // column itu kebawah atau vertical
        modifier = modifier
    ) {
        Row( // row itu kesamping atau horizontal
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomRadioButton(
                text = "Title",
                selected = selected is Title,
                onSelect = {
                    selected = Title
                    onOrderChange(selected, order)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            CustomRadioButton( // menggunakan Custom radio button yang telah dibuat
                text = "Date",
                selected = selected is Date,
                onSelect = {
                    selected = Date
                    onOrderChange(selected, order)
                }
            )
            Spacer(modifier = Modifier.width(8.dp)) // berfungsi sebagai jarak // width itu lebar & height itu tinggi
            CustomRadioButton(
                text = "Mood",
                selected = selected is Mood,
                onSelect = {
                    selected = Mood
                    onOrderChange(selected, order)
                }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomRadioButton(
                text = "Ascending",
                selected = ordered is Ascending,
                onSelect = {
                    ordered = Ascending
                    onOrderChange(selected, ordered)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            CustomRadioButton(
                text = "Descending",
                selected = ordered is Descending,
                onSelect = {
                    ordered = Descending
                    onOrderChange(selected, ordered)
                }
            )
        }
    }
}