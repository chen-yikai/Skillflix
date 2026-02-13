package dev.eliaschen.skillflix.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.eliaschen.skillflix.schema.VideoType

enum class Screen(val title: String) {
    Home("主頁面"),
    Collection("我的收藏"),
    ActualList("完整列表"),
    Detail("詳細資訊")
}

class NavViewModel : ViewModel() {
    private val initScreen = Screen.Home
    var currentScreen by mutableStateOf(initScreen)
        private set
    var navStack = mutableStateListOf(initScreen)
        private set
    var navType by mutableStateOf(VideoType.TVSeries)
    var navId by mutableStateOf("")

    fun navigate(screen: Screen, videoType: VideoType? = null, id: String = "") {
        currentScreen = screen
        if (videoType != null)
            navType = videoType
        navId = id
        navStack.add(screen)
    }

    fun pop() {
        if (navStack.size > 1) {
            navStack.removeAt(navStack.lastIndex)
            currentScreen = navStack.last()
        }
    }
}