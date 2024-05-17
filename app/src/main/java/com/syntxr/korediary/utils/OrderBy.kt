package com.syntxr.korediary.utils

sealed class OrderBy {
    data object Ascending : OrderBy()
    data object Descending : OrderBy()
}