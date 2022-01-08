package io.github.remotelight.api.models

import io.github.remotelight.core.constants.Defaults
import io.github.remotelight.core.output.scene.Scene
import io.github.remotelight.core.output.scene.SceneEntry
import java.util.*

data class SceneModel(
    val id: String?,
    val name: String?,
    val loopInterval: Long?,
    val entries: List<SceneEntry>?,
    val pixelCount: Int?
)

fun Scene.toModel() = SceneModel(id, name, loopInterval, entries, pixelCount)

fun SceneModel.toScene() = Scene(
    id ?: UUID.randomUUID().toString(),
    name ?: "",
    loopInterval ?: Defaults.SCENE_LOOP_INTERVAL,
    entries ?: emptyList(),
    pixelCount ?: 0
)

fun SceneModel.copyIntoScene(scene: Scene) = scene.copy(
    name = name ?: scene.name,
    loopInterval = loopInterval ?: scene.loopInterval,
    entries = entries ?: scene.entries,
    pixelCount = pixelCount ?: scene.pixelCount
)
