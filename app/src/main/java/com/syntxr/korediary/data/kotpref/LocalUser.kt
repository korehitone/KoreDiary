package com.syntxr.korediary.data.kotpref

import com.chibatching.kotpref.KotprefModel

object LocalUser : KotprefModel() {
    var uuid by stringPref("")
    var username by stringPref("")
    var email by stringPref("")
    var password by stringPref("")
}