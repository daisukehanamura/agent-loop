package dev.hanamaru.careerdeck.profile.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProfileJpaRepository : JpaRepository<ProfileEntity, UUID>
