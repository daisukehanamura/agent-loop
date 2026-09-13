package dev.hanamaru.careerdeck.profile.adapter.persistence

import dev.hanamaru.careerdeck.profile.domain.DisplayName
import dev.hanamaru.careerdeck.profile.domain.Headline
import dev.hanamaru.careerdeck.profile.domain.Profile
import dev.hanamaru.careerdeck.profile.domain.ProfileId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "profile")
class ProfileEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,
    @Column(name = "display_name", nullable = false, length = DisplayName.MAX_LENGTH)
    var displayName: String,
    @Column(name = "headline", nullable = false, length = Headline.MAX_LENGTH)
    var headline: String,
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,
) {
    fun toDomain(): Profile =
        Profile(
            id = ProfileId(id),
            displayName = DisplayName(displayName),
            headline = Headline(headline),
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    companion object {
        fun from(profile: Profile): ProfileEntity =
            ProfileEntity(
                id = profile.id.value,
                displayName = profile.displayName.value,
                headline = profile.headline.value,
                createdAt = profile.createdAt,
                updatedAt = profile.updatedAt,
            )
    }
}
