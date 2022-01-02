package io.github.remotelight.core.effect

fun interface EffectFactory<T : Effect> {

    fun createEffect(effectConfig: EffectConfig): T

}