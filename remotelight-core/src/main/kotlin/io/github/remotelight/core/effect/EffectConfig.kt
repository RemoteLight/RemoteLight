package io.github.remotelight.core.effect

import io.github.remotelight.core.config.Config
import io.github.remotelight.core.config.provider.PropertyProvider

class EffectConfig(
    propertyProvider: PropertyProvider<*>,
    val effectIdentifier: EffectIdentifier
) : Config(propertyProvider) {

    override val storeDefaultValue: Boolean = true

}