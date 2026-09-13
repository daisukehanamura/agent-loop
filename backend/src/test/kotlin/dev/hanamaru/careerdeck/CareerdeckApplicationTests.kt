package dev.hanamaru.careerdeck

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class CareerdeckApplicationTests {
    @Test
    fun `アプリケーションコンテキストが起動する`() {
    }
}
