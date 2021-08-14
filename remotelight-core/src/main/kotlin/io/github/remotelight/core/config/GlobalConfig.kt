package io.github.remotelight.core.config

import io.github.remotelight.core.config.loader.ConfigLoader
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

object GlobalConfig: Config(), KoinComponent {

    override fun getConfigLoader(): ConfigLoader {
        val loader: ConfigLoader by inject(qualifier = named("global"))
        return loader
    }

    val test: String by Property("test", "Test value").also { println("prop added") }

}