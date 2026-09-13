package dev.hanamaru.careerdeck.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/** 時刻はすべて Bean 経由で取得する（テストで固定できるようにするため）。 */
@Configuration(proxyBeanMethods = false)
class ClockConfig {
    @Bean
    fun clock(): Clock = Clock.systemUTC()
}
