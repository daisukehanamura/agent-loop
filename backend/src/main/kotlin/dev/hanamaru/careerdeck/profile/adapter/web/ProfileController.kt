package dev.hanamaru.careerdeck.profile.adapter.web

import dev.hanamaru.careerdeck.profile.application.ProfileService
import dev.hanamaru.careerdeck.profile.domain.DisplayName
import dev.hanamaru.careerdeck.profile.domain.Headline
import dev.hanamaru.careerdeck.profile.domain.ProfileId
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/profiles")
class ProfileController(
    private val service: ProfileService,
) {
    @GetMapping
    fun list(): List<ProfileResponse> = service.list().map(ProfileResponse::from)

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: UUID,
    ): ProfileResponse = ProfileResponse.from(service.get(ProfileId(id)))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateProfileRequest,
    ): ProfileResponse =
        ProfileResponse.from(
            service.register(DisplayName(request.displayName), Headline(request.headline)),
        )
}
