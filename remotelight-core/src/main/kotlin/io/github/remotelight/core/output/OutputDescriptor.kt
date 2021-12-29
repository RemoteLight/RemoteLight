package io.github.remotelight.core.output

interface OutputDescriptor {

    val uniqueIdentifier: OutputIdentifier

    val displayName: String

}

typealias OutputIdentifier = String
