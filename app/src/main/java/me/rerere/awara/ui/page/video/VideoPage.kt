package me.rerere.awara.ui.page.video

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.MediaItem
import androidx.media3.common.VideoSize
import kotlinx.coroutines.flow.collectLatest
import me.rerere.awara.data.entity.fixUrl
import me.rerere.awara.ui.LocalMessageProvider
import me.rerere.awara.ui.component.common.BackButton
import me.rerere.awara.ui.component.player.Player
import me.rerere.awara.ui.component.player.PlayerScaffold
import me.rerere.awara.ui.component.player.PlayerState
import me.rerere.awara.ui.component.player.rememberPlayerState
import me.rerere.awara.ui.hooks.ForceSystemBarColor
import me.rerere.awara.ui.hooks.rememberRequestedScreenOrientation
import me.rerere.awara.ui.hooks.rememberWindowSize
import me.rerere.awara.ui.page.video.layout.VideoPagePhoneLayout
import me.rerere.awara.ui.page.video.layout.VideoPageTabletLayout
import org.koin.androidx.compose.koinViewModel

@Composable
fun VideoPage(vm: VideoVM = koinViewModel()) {
    ForceSystemBarColor(appearanceLight = false)

    var requestOrientation by rememberRequestedScreenOrientation()
    val windowSize = rememberWindowSize()
    val state = rememberPlayerState()
    val message = LocalMessageProvider.current

    // Full Screen
    var fullscreen by remember { mutableStateOf(false) }
    fun enterFullScreen() {
        if(state.videoSize == VideoSize.UNKNOWN) return
        if(state.videoSize.width > state.videoSize.height) {
            fullscreen = true
            requestOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            fullscreen = true
            requestOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    fun exitFullScreen() {
        requestOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        fullscreen = false
    }

    // Handle VM Events
    LaunchedEffect(Unit) {
        vm.events.collectLatest {
            when (it) {
                // URL加载完成，更新播放器
                is VideoVM.VideoEvent.UrlLoaded -> {
                    if (it.urls.isEmpty()) {
                        message.error {
                            Text("No playable video found")
                        }
                        println("empty")
                    }
                    state.updatePlayerItems(
                        it.urls.map {
                            PlayerState.PlayerItem(
                                quality = it.name,
                                mediaItem = MediaItem.fromUri(it.src.view.fixUrl())
                            )
                        }
                    )
                    state.updateCurrentQuality(it.urls.lastOrNull()?.name ?: "Unknown")
                    state.prepare()
                    // TODO: Update current quality to user's preference
                }
            }
        }
    }

    // UI
    PlayerScaffold(
        fullscreen = fullscreen,
        player = {
            Player(
                state = state,
                modifier = Modifier.fillMaxSize(),
                navigationIcon = {
                    BackButton()
                },
                actions = {
                    IconButton(
                        onClick = {
                            if(!fullscreen) enterFullScreen() else exitFullScreen()
                        }
                    ) {
                        Icon(
                            if(!fullscreen) Icons.Outlined.Fullscreen else Icons.Outlined.FullscreenExit,
                            "Fullscreen"
                        )
                    }

                    Box {
                        var expandedVideoQuality by remember {
                            mutableStateOf(false)
                        }
                        TextButton(
                            onClick = {
                                expandedVideoQuality = true
                            }
                        ) {
                            Text(
                                text = state.currentQuality,
                                color = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = expandedVideoQuality,
                            onDismissRequest = { expandedVideoQuality = false }
                        ) {
                            vm.state.urls.forEach { url ->
                                DropdownMenuItem(
                                    onClick = {
                                        expandedVideoQuality = false
                                        state.updateCurrentQuality(url.name)
                                        state.prepare()
                                    },
                                    text = {
                                        Text(url.name)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { player ->
        when(windowSize.widthSizeClass) {
            WindowWidthSizeClass.Compact -> VideoPagePhoneLayout(vm, player)
            else -> VideoPageTabletLayout(vm, player)
        }
    }
}