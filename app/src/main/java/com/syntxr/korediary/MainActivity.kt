package com.syntxr.korediary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.syntxr.korediary.ui.theme.KoreDiaryTheme
import com.syntxr.korediary.utils.GlobalState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // memanggil splash screen
        installSplashScreen()
        setContent {
            KoreDiaryTheme( // tema
                appTheme = GlobalState.theme // memanggil tema yang terpilih
            )
            {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KoreApp() // composable App
                }
            }
        }
    }
}
