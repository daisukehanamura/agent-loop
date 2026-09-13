package dev.hanamaru.careerdeck.profile.adapter.persistence

import dev.hanamaru.careerdeck.profile.application.port.ProfileRepository
import dev.hanamaru.careerdeck.profile.domain.Profile
import dev.hanamaru.careerdeck.profile.domain.ProfileId
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class ProfilePersistenceAdapter(
    private val jpaRepository: ProfileJpaRepository,
) : ProfileRepository {
    override fun save(profile: Profile): Profile = jpaRepository.save(ProfileEntity.from(profile)).toDomain()

    override fun findById(id: ProfileId): Profile? = jpaRepository.findById(id.value).getOrNull()?.toDomain()

    override fun findAll(): List<Profile> =
        jpaRepository
            .findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
            .map { it.toDomain() }
}
