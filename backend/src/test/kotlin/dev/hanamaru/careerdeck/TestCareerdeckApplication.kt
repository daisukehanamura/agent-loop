package dev.hanamaru.careerdeck

import org.springframework.boot.fromApplication
import org.springframework.boot.with

/** IDE から Testcontainers 付きでアプリを起動するエントリポイント。 */
fun main(args: Array<String>) {
    fromApplication<CareerdeckApplication>().with(TestcontainersConfiguration::class).run(*args)
}
