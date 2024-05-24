package com.syntxr.korediary.data.kotpref

import androidx.annotation.DrawableRes
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumOrdinalPref
import com.syntxr.korediary.R

object GlobalPreferences : KotprefModel() {
    var theme by enumOrdinalPref(AppTheme.DEFAULT_DARK)
    var isOnBoarding by booleanPref(true)

    override fun clear() {
        super.clear()
        theme = AppTheme.DEFAULT_DARK
        isOnBoarding = true
    }

    enum class AppTheme(
        val id : String,
        @DrawableRes val background : Int
    ) {
        DEFAULT_LIGHT( // nama class baru, silahkan kalian beri nama
            "Default - Light",  // karena perlu string, maka diisi dengan nama tema
            R.drawable.fuji_sakura // karena memerlukan gambar, seperti ini cara memanggilnya
        ),
        DEFAULT_DARK(
            "Default - Dark",
            R.drawable.fuji_sakura
        ),
        MOUNTAIN_LIGHT(
            "Mountain - Light",
            R.drawable.mountain_light
        ),
        MOUNTAIN_DARK(
            "Mountain - Dark",
            R.drawable.mountain
        ),
        SAKURA_LIGHT(
            "Sakura - Light",
            R.drawable.fuji_sakura
        ),
        SAKURA_DARK(
            "Sakura - Dark",
            R.drawable.sakura_night
        ),
        DYNAMIC(
            "dynamic",
            R.drawable.forest
        )
    }
}