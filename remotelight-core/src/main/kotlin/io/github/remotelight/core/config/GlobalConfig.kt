package io.github.remotelight.core.config

import io.github.remotelight.core.config.property.Property
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.get
import org.koin.core.qualifier.named

object GlobalConfig : KoinComponent, Config(
    get().get(qualifier = named("global"))
) {

    val test by Property("test", "Hello World!")

}