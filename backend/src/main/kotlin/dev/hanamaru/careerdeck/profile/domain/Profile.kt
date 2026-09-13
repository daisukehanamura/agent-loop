package dev.hanamaru.careerdeck.profile.domain

import java.time.Instant
import java.util.UUID

/**
 * プロフィールのドメインモデル。
 *
 * このパッケージはフレームワーク非依存を保つ（ArchUnit で検証している）。
 */
data class Profile(
    val id: ProfileId,
    val displayName: DisplayName,
    val headline: Headline,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun create(
            displayName: DisplayName,
            headline: Headline,
            now: Instant,
        ): Profile = Profile(ProfileId.new(), displayName, headline, now, now)
    }
}

@JvmInline
value class ProfileId(
    val value: UUID,
) {
    companion object {
        fun new(): ProfileId = ProfileId(UUID.randomUUID())
    }
}

@JvmInline
value class DisplayName(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "displayName は空にできない" }
        require(value.length <= MAX_LENGTH) { "displayName は $MAX_LENGTH 文字以内" }
    }

    companion object {
        const val MAX_LENGTH = 100
    }
}

@JvmInline
value class Headline(
    val value: String,
) {
    init {
        require(value.length <= MAX_LENGTH) { "headline は $MAX_LENGTH 文字以内" }
    }

    companion object {
        const val MAX_LENGTH = 200
    }
}
