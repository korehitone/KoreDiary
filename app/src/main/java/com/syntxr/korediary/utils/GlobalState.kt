package com.syntxr.korediary.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.syntxr.korediary.data.kotpref.GlobalPreferences
import com.syntxr.korediary.data.kotpref.LocalUser

object GlobalState {
    var theme by mutableStateOf(GlobalPreferences.theme)
    var username by mutableStateOf(LocalUser.username)
    var email by mutableStateOf(LocalUser.email)
    var password by mutableStateOf(LocalUser.password)
    var isOnBoarding by mutableStateOf(GlobalPreferences.isOnBoarding)
}