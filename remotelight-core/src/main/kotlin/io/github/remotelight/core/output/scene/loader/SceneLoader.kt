package io.github.remotelight.core.output.scene.loader

import io.github.remotelight.core.io.Loader
import io.github.remotelight.core.output.scene.Scene

interface SceneLoader : Loader {

    fun loadScenes(): List<Scene>?

    fun storeScenes(scenes: List<Scene>)

}