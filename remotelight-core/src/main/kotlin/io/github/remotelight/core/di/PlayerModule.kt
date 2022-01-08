package io.github.remotelight.core.di

import io.github.remotelight.core.player.PlayerManager
import org.koin.dsl.module

val playerModule = module {
    single { PlayerManager(get(), get()) }
}