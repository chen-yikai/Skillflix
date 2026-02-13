package dev.eliaschen.skillflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import dev.eliaschen.skillflix.screen.ActualList
import dev.eliaschen.skillflix.screen.Collection
import dev.eliaschen.skillflix.screen.Detail
import dev.eliaschen.skillflix.screen.Home
import dev.eliaschen.skillflix.ui.theme.SkillflixTheme
import dev.eliaschen.skillflix.viewmodel.NavViewModel
import dev.eliaschen.skillflix.viewmodel.NetworkViewModel
import dev.eliaschen.skillflix.viewmodel.Screen

val LocalNavViewModel = compositionLocalOf<NavViewModel> { error("nav viewmodel") }
val LocalNetworkViewModel = compositionLocalOf<NetworkViewModel> { error("network viewmodel") }

val red = Color(0xffEF2A25)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillflixTheme {
                val nav: NavViewModel by viewModels()
                val api: NetworkViewModel by viewModels()

                CompositionLocalProvider(
                    LocalNavViewModel provides nav,
                    LocalNetworkViewModel provides api
                ) {
                    Crossfade(nav.currentScreen) { screen ->
                        when (screen) {
                            Screen.Home -> Home()
                            Screen.Collection -> Collection()
                            Screen.ActualList -> ActualList()
                            Screen.Detail -> Detail()
                        }
                    }
                }
            }
        }
    }
}