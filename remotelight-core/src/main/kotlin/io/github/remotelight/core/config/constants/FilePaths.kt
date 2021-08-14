package io.github.remotelight.core.config.constants

import io.github.remotelight.core.RemoteLightCore
import java.io.File

object FilePaths {

    var ROOT_LOCATION: String = System.getProperty("user.home")
        set(value) { field = throwOnInit(value).toFile().absolutePath }

    var ROOT_DIR_NAME = ".RemoteLight"
        set(value) { field = throwOnInit(value) }

    val ROOT_PATH
        get() = ROOT_LOCATION.separate(ROOT_DIR_NAME)

    var GLOBAL_CONFIG_NAME = "config.json"
        set(value) { field = throwOnInit(value) }

    val GLOBAL_CONFIG_PATH
        get() = ROOT_PATH.separate(GLOBAL_CONFIG_NAME)

    /**
     * Verifies that [RemoteLightCore] is not initialized. If so, an [IllegalStateException] will be thrown.
     */
    private fun <T> throwOnInit(value: T): T {
        if(RemoteLightCore.isInitialized)
            throw IllegalStateException("Path cannot be changed during runtime!")
        return value
    }

    private fun String.separate(subPath: String): String = "${this}${File.separator}${subPath}"

    fun String.toFile() = File(this)

    fun File.mkdirsParents(): File {
        this.absoluteFile.parentFile.mkdirs()
        return this
    }
}