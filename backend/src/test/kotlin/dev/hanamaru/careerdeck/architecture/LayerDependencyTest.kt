package dev.hanamaru.careerdeck.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

/**
 * 設計ルールをテストとして固定する。
 * 「ドキュメントに書いたが守られない」を防ぐのが目的で、CI で落ちる。
 *
 * @ArchTest フィールド名は失敗時にそのまま表示されるため、日本語でルールを表現している
 * （そのため ktlint の property-naming は意図的に抑制する）。
 */
@Suppress("ktlint:standard:property-naming")
@AnalyzeClasses(
    packages = ["dev.hanamaru.careerdeck"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
class LayerDependencyTest {
    @ArchTest
    val `domain はフレームワークに依存しない`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "jakarta.persistence..",
                "tools.jackson..",
                "com.fasterxml.jackson..",
            )

    @ArchTest
    val `domain は上位レイヤに依存しない`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..application..", "..adapter..")

    @ArchTest
    val `application は adapter に依存しない`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..adapter..")

    @ArchTest
    val `web アダプタは persistence アダプタに直接依存しない`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..adapter.web..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..adapter.persistence..")

    @ArchTest
    val `JPA エンティティは persistence アダプタの中だけに置く`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith(jakarta.persistence.Entity::class.java)
            .should()
            .resideInAPackage("..adapter.persistence..")

    @ArchTest
    val `Controller は RestController だけにする`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .beAnnotatedWith(org.springframework.web.bind.annotation.RestController::class.java)
}
