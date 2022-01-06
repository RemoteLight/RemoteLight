package io.github.remotelight.core.player

import io.github.remotelight.core.color.ColorBase
import kotlinx.coroutines.CoroutineScope

interface PlaybackContent {

    val playbackPriority: Int

    fun hasContent(): Boolean

    fun onPlayerEnable(playbackScope: CoroutineScope) {}

    fun produce(): Array<out ColorBase>?

    fun onPlayerDisable() {}

    fun getDescription(): String

}