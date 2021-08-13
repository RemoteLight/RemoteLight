package io.github.remotelight.core

import org.tinylog.kotlin.Logger


class RemoteLightCore {

    companion object {
        val VERSION = "1.0.0"
    }

    init {
        Logger.info("Initialized RemoteLightCore version $VERSION")
    }

}