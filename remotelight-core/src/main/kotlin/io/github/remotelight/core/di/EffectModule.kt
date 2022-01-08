package io.github.remotelight.core.di

import io.github.remotelight.core.effect.DefaultEffectRegistry
import io.github.remotelight.core.effect.EffectRegistry
import org.koin.dsl.module

val effectModule = module {
    single<EffectRegistry> { DefaultEffectRegistry }
}
