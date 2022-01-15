package io.github.remotelight.controller.di

import io.github.remotelight.controller.output.OutputController
import org.koin.dsl.module

val controllerModule = module {
    factory { OutputController() }
}
