package io.github.remotelight.core.output.scene

import io.github.remotelight.core.di.configModule
import io.github.remotelight.core.output.scene.loader.SceneLoader
import io.github.remotelight.core.tools.NoDelayDebounce
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.junit5.KoinTestExtension
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class SceneManagerTest {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(configModule, module {
            factory<Debounce<Unit>> { (scope: CoroutineScope) -> NoDelayDebounce() }
        })
    }

    @Test
    fun addRemoveTest() {
        val loader = TestSceneLoader(2)
        val manager = SceneManager(loader)

        manager.loadScenes()
        assertEquals(2, manager.getScenes().size)

        loader.testScenes.clear()
        val newScene = createTestScene("New")
        manager.addScene(newScene)
        assertEquals(3, manager.getScenes().size)
        assertEquals(3, loader.testScenes.size)

        assertTrue(manager.hasScene(newScene.id))
        assertFalse(manager.hasScene("Invalid ID"))

        assertTrue(manager.removeScene(newScene.id))
        assertEquals(2, loader.testScenes.size)
        assertFalse(manager.removeScene(newScene.id))
    }

    companion object {
        fun createTestScene(name: String, entries: List<SceneEntry> = emptyList(), pixelCount: Int = 0) = Scene(
            UUID.randomUUID().toString(),
            name,
            entries,
            pixelCount
        )
    }

    internal class TestSceneLoader(scenesAmount: Int = 2) : SceneLoader {
        val testScenes = List(scenesAmount) {
            createTestScene("Scene #$it")
        }.toMutableList()

        override fun loadScenes(): List<Scene> = testScenes

        override fun storeScenes(scenes: List<Scene>) {
            testScenes.clear()
            testScenes.addAll(scenes)
        }

        override fun getSource() = "Test Scene Loader"
    }

}