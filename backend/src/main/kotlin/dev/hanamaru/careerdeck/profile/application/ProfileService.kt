package dev.hanamaru.careerdeck.profile.application

import dev.hanamaru.careerdeck.profile.application.port.ProfileRepository
import dev.hanamaru.careerdeck.profile.domain.DisplayName
import dev.hanamaru.careerdeck.profile.domain.Headline
import dev.hanamaru.careerdeck.profile.domain.Profile
import dev.hanamaru.careerdeck.profile.domain.ProfileId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional(readOnly = true)
class ProfileService(
    private val repository: ProfileRepository,
    private val clock: Clock,
) {
    fun list(): List<Profile> = repository.findAll()

    fun get(id: ProfileId): Profile = repository.findById(id) ?: throw ProfileNotFoundException(id)

    @Transactional
    fun register(
        displayName: DisplayName,
        headline: Headline,
    ): Profile = repository.save(Profile.create(displayName, headline, clock.instant()))
}

class ProfileNotFoundException(
    id: ProfileId,
) : RuntimeException("profile not found: ${id.value}")
