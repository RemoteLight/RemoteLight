package io.github.remotelight.core.config.property

import io.github.remotelight.core.config.Config
import io.github.remotelight.core.config.TestConfigLoader

abstract class BasePropertyTest {

    protected open val testConfig = object : Config(TestConfigLoader()) {}

}