plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover)
}

group = "dev.hanamaru"
version = "0.0.1-SNAPSHOT"
description = "CareerDeck - skill and career portfolio service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation(libs.springdoc.openapi.webmvc.ui)
    implementation(libs.anthropic.java)

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation(libs.archunit.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.assertions.core)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
        allWarningsAsErrors = true
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

// ---------------------------------------------------------------- テスト
tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// ---------------------------------------------------------------- フォーマット / 静的解析
// Spotless + ktlint が整形と標準ルールを担当する。
// detekt (コードスメル検出) は 1.23.x が Kotlin 2.0.21 ビルドで Kotlin 2.3 と非互換、
// 2.0.0-alpha は configuration-cache 非対応のため未導入。2.0 stable が出たら追加する。
spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint(libs.versions.ktlint.get())
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
    }
}

// ---------------------------------------------------------------- カバレッジ (Kover)
kover {
    reports {
        filters {
            excludes {
                classes(
                    "dev.hanamaru.careerdeck.CareerdeckApplicationKt",
                    "dev.hanamaru.careerdeck.*.adapter.persistence.*Entity",
                    "*Configuration",
                )
            }
        }
        verify {
            rule {
                minBound(60) // TODO: 機能が増えるたびに引き上げる
            }
        }
    }
}

// ---------------------------------------------------------------- 集約タスク
tasks.register("checkAll") {
    group = "verification"
    description = "CI と同じ検証をローカルで一括実行する"
    dependsOn("spotlessCheck", "test", "koverVerify")
}
