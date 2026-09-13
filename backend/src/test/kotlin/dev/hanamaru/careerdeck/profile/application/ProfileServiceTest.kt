package dev.hanamaru.careerdeck.profile.application

import dev.hanamaru.careerdeck.profile.application.port.ProfileRepository
import dev.hanamaru.careerdeck.profile.domain.DisplayName
import dev.hanamaru.careerdeck.profile.domain.Headline
import dev.hanamaru.careerdeck.profile.domain.Profile
import dev.hanamaru.careerdeck.profile.domain.ProfileId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ProfileServiceTest {
    private val fixedNow = Instant.parse("2026-09-13T12:00:00Z")
    private val clock = Clock.fixed(fixedNow, ZoneOffset.UTC)
    private val repository = mockk<ProfileRepository>()
    private val service = ProfileService(repository, clock)

    @Test
    fun `register は Clock の時刻でプロフィールを保存する`() {
        val saved = slot<Profile>()
        every { repository.save(capture(saved)) } answers { saved.captured }

        val result = service.register(DisplayName("hanamaru"), Headline("SRE"))

        result.displayName.value shouldBe "hanamaru"
        saved.captured.createdAt shouldBe fixedNow
        verify(exactly = 1) { repository.save(any()) }
    }

    @Test
    fun `get は存在しない ID で ProfileNotFoundException を投げる`() {
        val id = ProfileId.new()
        every { repository.findById(id) } returns null

        shouldThrow<ProfileNotFoundException> { service.get(id) }
    }
}
