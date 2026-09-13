package dev.hanamaru.careerdeck.profile.application.port

import dev.hanamaru.careerdeck.profile.domain.Profile
import dev.hanamaru.careerdeck.profile.domain.ProfileId

/** 永続化の出力ポート。実装は adapter.persistence 側にある。 */
interface ProfileRepository {
    fun save(profile: Profile): Profile

    fun findById(id: ProfileId): Profile?

    fun findAll(): List<Profile>
}
