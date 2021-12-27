package io.github.remotelight.core.config

import io.github.remotelight.core.RemoteLightCore

data class PropertyValuesWrapper(
    val version: String = RemoteLightCore.VERSION,
    val properties: Map<String, Any?>
)