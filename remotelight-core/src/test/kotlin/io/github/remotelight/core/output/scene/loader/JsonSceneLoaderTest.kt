package io.github.remotelight.core.output.scene.loader

import io.github.remotelight.core.di.configModule
import io.github.remotelight.core.output.scene.SceneEntry
import io.github.remotelight.core.output.scene.SceneManagerTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.component.get
import org.koin.test.junit5.AutoCloseKoinTest
import org.koin.test.junit5.KoinTestExtension
import java.io.File
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class JsonSceneLoaderTest : AutoCloseKoinTest() {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(configModule)
    }

    @Test
    fun storeLoadScenes() {
        val file = File("build/resources/test", "test_scenes_stored.json")
        println("Test file location: ${file.absolutePath}")

        val testScenes = List(5) {
            val entries = List(2) { i ->
                SceneEntry("test_output_$i", (1..60).map { Random.nextInt() })
            }
            SceneManagerTest.createTestScene("Scene #$it", entries)
        }

        val loader = JsonSceneLoader(file, get())
        loader.storeScenes(testScenes)
        assertTrue(file.isFile)

        val loadedScenes = loader.loadScenes()
        assertNotNull(loadedScenes)
        assertContentEquals(testScenes, loadedScenes)
    }

}