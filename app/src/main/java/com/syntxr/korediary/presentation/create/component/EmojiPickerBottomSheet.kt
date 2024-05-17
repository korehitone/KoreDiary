package com.syntxr.korediary.presentation.create.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ralphordanza.emojipickercompose.EmojiPickerView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet


@Destination(style = DestinationStyleBottomSheet::class)
// karena library emoji membutuhkan bottomsheet, kita membuat ini
// raamcosta menyediakan destination untuk bottom sheet
@Composable
fun EmojiPickerBottomSheet(resultNavigator: ResultBackNavigator<String>) { // ResultBackNavigator agar bisa mengirim nilai ke screen
    Box(modifier = Modifier.defaultMinSize(minHeight = 1.dp)) {
        EmojiPickerView(
            onEmojiSelect = { unicode ->
                resultNavigator.navigateBack(unicode) // mengirim nilai dari bottom sheet ke screen
            },
        )
    }
}