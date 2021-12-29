package io.github.remotelight.core.function

abstract class FunctionManager<T : Function> {

    abstract fun getStatus(): Status

}