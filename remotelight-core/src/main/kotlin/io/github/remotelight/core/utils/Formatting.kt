package io.github.remotelight.core.utils

import io.github.remotelight.core.output.Output

fun Output.formatOutputInfo() = "'${config.name}' (ID: ${config.id}, Type: ${config.outputIdentifier})"
