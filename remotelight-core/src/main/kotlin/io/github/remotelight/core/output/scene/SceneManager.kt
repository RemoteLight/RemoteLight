package io.github.remotelight.core.output.scene

import io.github.remotelight.core.output.Output
import io.github.remotelight.core.output.scene.loader.SceneLoader
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.tinylog.kotlin.Logger

class SceneManager(
    private val sceneLoader: SceneLoader
) : KoinComponent {

    private val scenes = mutableListOf<Scene>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val debounce by inject<Debounce<Unit>> { parametersOf(scope) }

    @Synchronized
    fun loadScenes() {
        Logger.info("Loading scenes from ${sceneLoader.getSource()}...")
        val loadedScenes = sceneLoader.loadScenes()
        if (loadedScenes.isNullOrEmpty()) {
            Logger.info("No scenes available.")
        } else {
            scenes.addAll(loadedScenes)
            Logger.info("Successfully loaded ${loadedScenes.size} scenes.")
        }
    }

    @Synchronized
    fun storeScenes() {
        debounce.debounce {
            Logger.trace("Storing ${scenes.size} scenes to ${sceneLoader.getSource()}...")
            sceneLoader.storeScenes(scenes)
        }
    }

    fun getScenes() = scenes.toList()

    fun getScene(sceneId: String) = scenes.find { it.id == sceneId }

    fun hasScene(sceneId: String) = getScene(sceneId) != null

    fun addScene(scene: Scene) {
        if (hasScene(scene.id)) {
            throw IllegalArgumentException("Scene with ID ${scene.id} already exists.")
        }
        scenes.add(scene)
        onSceneChanged(scene)
    }

    fun removeScene(scene: Scene): Boolean {
        val success = scenes.remove(scene)
        if (success) {
            onSceneDeleted(scene)
        }
        return success
    }

    fun removeScene(sceneId: String): Boolean {
        val scene = getScene(sceneId) ?: return false
        return removeScene(scene)
    }

    private fun onSceneChanged(scene: Scene) {
        Logger.trace("Scene changed: $scene")
        storeScenes()
    }

    private fun onSceneDeleted(scene: Scene) {
        Logger.info("Scene ${scene.name} (${scene.id}) deleted.")
        storeScenes()
    }

    fun verifyScenes(availableOutputs: List<Output>): List<String> {
        val invalidSceneIds = mutableListOf<String>()
        scenes.forEach { scene ->
            val invalidSceneEntries = scene.verifyEntries(availableOutputs)
            if (invalidSceneEntries.isNotEmpty()) {
                invalidSceneIds.add(scene.id)
                Logger.debug("Found invalid scene entries: $invalidSceneEntries")
                Logger.debug("Please make sure all outputs used in the scene exist.")
            }
        }
        return invalidSceneIds
    }

    fun cancelScope() {
        scope.cancel()
    }

}