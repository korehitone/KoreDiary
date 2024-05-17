package com.syntxr.korediary.utils

sealed class DiaryOrder {
    data object Title : DiaryOrder()
    data object Date : DiaryOrder()
    data object Mood : DiaryOrder()
}