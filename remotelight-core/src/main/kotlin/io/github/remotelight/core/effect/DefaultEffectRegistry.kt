package io.github.remotelight.core.effect

import io.github.remotelight.core.effect.animation.Rainbow

object DefaultEffectRegistry : EffectRegistry {

    private val effectFactories = mutableMapOf<EffectIdentifier, EffectFactory<*>>()

    override fun createEffect(effectIdentifier: EffectIdentifier, effectConfig: EffectConfig): Effect? {
        return effectFactories[effectIdentifier]?.createEffect(effectConfig)
    }

    override fun getRegisteredEffects(): List<EffectIdentifier> = effectFactories.keys.toList()

    override fun registerEffect(effectIdentifier: EffectIdentifier, factory: EffectFactory<*>) {
        if (effectFactories.containsKey(effectIdentifier)) {
            throw IllegalArgumentException("EffectFactory for the effect type '$effectIdentifier' is already registered.")
        }
        effectFactories[effectIdentifier] = factory
    }

    fun registerEffect(name: String, category: EffectCategory = EffectCategory.Animation, factory: EffectFactory<*>) {
        registerEffect(EffectIdentifier(name, category), factory)
    }

    init {
        initDefaultEffects()
    }

    private fun initDefaultEffects() {
        registerEffect("Rainbow") { Rainbow(it) }
    }

}