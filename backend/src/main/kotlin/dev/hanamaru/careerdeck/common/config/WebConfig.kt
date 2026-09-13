package dev.hanamaru.careerdeck.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@ConfigurationProperties(prefix = "careerdeck.web")
data class WebProperties(
    /** SPA の開発サーバーなど、CORS を許可するオリジン。 */
    val allowedOrigins: List<String> = listOf("http://localhost:5173"),
)

@Configuration(proxyBeanMethods = false)
class WebConfig(
    private val properties: WebProperties,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/api/**")
            .allowedOrigins(*properties.allowedOrigins.toTypedArray())
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
            .allowCredentials(true)
    }
}
