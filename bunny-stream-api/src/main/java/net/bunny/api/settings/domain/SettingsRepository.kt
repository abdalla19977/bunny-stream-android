package net.bunny.api.settings.domain

import arrow.core.Either
import net.bunny.api.settings.domain.model.PlayerSettings

interface SettingsRepository {
    suspend fun fetchSettings(libraryId: Long, videoId: String): Either<String, PlayerSettings>
    suspend fun fetchSettingsWithToken(
        libraryId: Long,
        videoId: String,
        token: String,
        expires: Long
    ): Either<String, PlayerSettings>
}