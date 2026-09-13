package dev.hanamaru.careerdeck.profile.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant

class ProfileTest {
    @Test
    fun `create は createdAt と updatedAt を同じ時刻にする`() {
        val now = Instant.parse("2026-09-13T00:00:00Z")

        val profile = Profile.create(DisplayName("hanamaru"), Headline("Backend Engineer"), now)

        profile.createdAt shouldBe now
        profile.updatedAt shouldBe now
    }

    @Test
    fun `displayName は空文字を拒否する`() {
        shouldThrow<IllegalArgumentException> { DisplayName(" ") }
    }

    @Test
    fun `displayName は上限文字数を超えると拒否する`() {
        shouldThrow<IllegalArgumentException> { DisplayName("a".repeat(DisplayName.MAX_LENGTH + 1)) }
    }

    @Test
    fun `headline は空文字を許可する`() {
        Headline("").value shouldBe ""
    }
}
