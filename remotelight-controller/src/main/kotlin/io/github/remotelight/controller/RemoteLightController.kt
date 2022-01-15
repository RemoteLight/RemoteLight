package io.github.remotelight.controller

import io.github.remotelight.controller.di.controllerModule
import org.koin.core.context.loadKoinModules

object RemoteLightController {

    init {
        loadKoinModules(controllerModule)
    }

}