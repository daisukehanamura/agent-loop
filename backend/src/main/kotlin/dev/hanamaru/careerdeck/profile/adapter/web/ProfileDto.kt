package dev.hanamaru.careerdeck.profile.adapter.web

import dev.hanamaru.careerdeck.profile.domain.DisplayName
import dev.hanamaru.careerdeck.profile.domain.Headline
import dev.hanamaru.careerdeck.profile.domain.Profile
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class ProfileResponse(
    val id: UUID,
    val displayName: String,
    val headline: String,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun from(profile: Profile): ProfileResponse =
            ProfileResponse(
                id = profile.id.value,
                displayName = profile.displayName.value,
                headline = profile.headline.value,
                createdAt = profile.createdAt,
                updatedAt = profile.updatedAt,
            )
    }
}

data class CreateProfileRequest(
    @field:NotBlank
    @field:Size(max = DisplayName.MAX_LENGTH)
    val displayName: String,
    @field:Size(max = Headline.MAX_LENGTH)
    val headline: String = "",
)
