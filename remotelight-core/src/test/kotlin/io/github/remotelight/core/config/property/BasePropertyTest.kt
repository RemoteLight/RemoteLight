package io.github.remotelight.core.config.property

import io.github.remotelight.core.config.BaseConfigTest
import io.github.remotelight.core.config.Config

internal abstract class BasePropertyTest : BaseConfigTest() {

    protected open val testConfig = object : Config(TestPropertyProvider()) {}

}