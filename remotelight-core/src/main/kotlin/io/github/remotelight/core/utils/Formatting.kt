package io.github.remotelight.core.utils

import io.github.remotelight.core.output.Output
import io.github.remotelight.core.output.scene.Scene

fun Output.formatInfo() = "'${config.name}' (ID: ${config.id}, Type: ${config.outputIdentifier})"

fun Scene.formatInfo() = "'$name' ($id)"
