package dev.hanamaru.careerdeck.profile.adapter.web

import dev.hanamaru.careerdeck.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.transaction.annotation.Transactional

/**
 * 実 Postgres (Testcontainers) + Flyway を通した結合テスト。
 * モックを挟まないので「本当に動くか」をここで担保する。
 */
@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfileControllerIntegrationTest {
    @Autowired
    private lateinit var mvc: MockMvcTester

    @Test
    fun `POST でプロフィールを作成し GET で取得できる`() {
        mvc
            .post()
            .uri("/api/profiles")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"displayName":"hanamaru","headline":"Backend Engineer"}""")
            .assertThat()
            .hasStatus(HttpStatus.CREATED)
            .bodyJson()
            .extractingPath("$.displayName")
            .isEqualTo("hanamaru")

        mvc
            .get()
            .uri("/api/profiles")
            .assertThat()
            .hasStatusOk()
            .bodyJson()
            .extractingPath("$[0].displayName")
            .isEqualTo("hanamaru")
    }

    @Test
    fun `displayName が空なら 400 を返す`() {
        mvc
            .post()
            .uri("/api/profiles")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"displayName":"","headline":""}""")
            .assertThat()
            .hasStatus(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `存在しない ID は 404 を返す`() {
        mvc
            .get()
            .uri("/api/profiles/{id}", "00000000-0000-0000-0000-000000000000")
            .assertThat()
            .hasStatus(HttpStatus.NOT_FOUND)
    }
}
