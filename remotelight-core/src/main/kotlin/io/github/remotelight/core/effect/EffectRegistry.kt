package io.github.remotelight.core.effect

interface EffectRegistry {

    fun createEffect(effectIdentifier: EffectIdentifier, effectConfig: EffectConfig): Effect?

    fun registerEffect(effectIdentifier: EffectIdentifier, factory: EffectFactory<*>)

}