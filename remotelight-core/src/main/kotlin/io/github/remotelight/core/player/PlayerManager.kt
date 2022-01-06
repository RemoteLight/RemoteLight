package io.github.remotelight.core.player

import io.github.remotelight.core.output.OutputManager
import io.github.remotelight.core.output.scene.Scene
import io.github.remotelight.core.output.scene.SceneManager
import io.github.remotelight.core.utils.formatInfo

class PlayerManager(
    private val sceneManager: SceneManager,
    private val outputManager: OutputManager
) {

    private val activePlayer = mutableListOf<ScenePlayer>()

    fun getActivePlayer() = activePlayer.toList()

    fun hasPlayerForScene(sceneId: String) = activePlayer.any { it.scene.id == sceneId }

    fun getPlayerForScene(sceneId: String) = activePlayer.find { it.scene.id == sceneId }

    suspend fun startPlayer(scene: Scene, vararg content: PlaybackContent): ScenePlayer {
        if (hasPlayerForScene(scene.id)) {
            throw IllegalStateException("There is already a player for scene ${scene.formatInfo()}.")
        }
        val player = createScenePlayer(scene)
        content.forEach { player.addContent(it) }
        player.start()
        return player
    }

    suspend fun startPlayer(sceneId: String) {
        val scene = sceneManager.getScene(sceneId)
            ?: throw IllegalArgumentException("The scene with ID $sceneId does not exist.")
        startPlayer(scene)
    }

    suspend fun stopPlayer(sceneId: String) {
        val player =
            getPlayerForScene(sceneId) ?: throw IllegalStateException("There is no active player for the scene $sceneId.")
        player.stop()
        activePlayer.remove(player)
    }

    suspend fun stopPlayer(scene: Scene) {
        stopPlayer(scene.id)
    }

    private fun createScenePlayer(scene: Scene): ScenePlayer {
        val outputs = scene.entries.map {
            outputManager.getOutputById(it.outputId)
                ?: throw IllegalStateException("Missing output ${it.outputId} for scene ${scene.formatInfo()}.")
        }
        return ScenePlayer(scene, outputs)
    }

}