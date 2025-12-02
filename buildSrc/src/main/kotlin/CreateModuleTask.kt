import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class CreateModuleTask : DefaultTask() {

    @get:Input
    @set:Option(option = "moduleName", description = "Name of the module to create")
    var moduleName: String = ""

    init {
        group = "scaffolding"
        description = "Creates a new feature module with standard directory structure"
    }

    @TaskAction
    fun createModule() {
        if (moduleName.isBlank()) {
            throw IllegalArgumentException("Module name is required. Use: ./gradlew createModule --moduleName=<name>")
        }

        val normalizedName = moduleName.lowercase().replace("-", "").replace("_", "")
        val moduleDir = File(project.rootDir, "modules/$moduleName")

        if (moduleDir.exists()) {
            throw IllegalArgumentException("Module '$moduleName' already exists at ${moduleDir.absolutePath}")
        }

        println("Creating module: $moduleName")

        // Create directory structure
        val directories = listOf(
            "src/main/java/me/karun/bank/credit/$normalizedName",
            "src/main/resources",
            "src/main/resources/db/changelog",
            "src/test/java/me/karun/bank/credit/$normalizedName"
        )

        directories.forEach { dir ->
            File(moduleDir, dir).mkdirs()
            println("  Created: modules/$moduleName/$dir")
        }

        // Create build.gradle.kts
        val buildGradle = File(moduleDir, "build.gradle.kts")
        buildGradle.writeText(generateBuildGradle())
        println("  Created: modules/$moduleName/build.gradle.kts")

        // Create package-info.java
        val packageInfo = File(moduleDir, "src/main/java/me/karun/bank/credit/$normalizedName/package-info.java")
        packageInfo.writeText(generatePackageInfo(normalizedName))
        println("  Created: modules/$moduleName/src/main/java/me/karun/bank/credit/$normalizedName/package-info.java")

        // Create module changelog
        val changelog = File(moduleDir, "src/main/resources/db/changelog/$moduleName.changelog.xml")
        changelog.writeText(generateChangelog())
        println("  Created: modules/$moduleName/src/main/resources/db/changelog/$moduleName.changelog.xml")

        println("\nModule '$moduleName' created successfully!")
        println("Don't forget to add the module's changelog to the master changelog in api-gateway.")
    }

    private fun generateBuildGradle(): String = """
plugins {
    `java-library`
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

val springBootVersion: String by project

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${'$'}springBootVersion")
    }
}

dependencies {
    api(project(":shared:kernel"))
    api(project(":shared:events"))
    implementation(project(":shared:infrastructure"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
""".trimIndent()

    private fun generatePackageInfo(normalizedName: String): String = """
/**
 * $moduleName module for the Credit Card Lending Platform.
 */
package me.karun.bank.credit.$normalizedName;
""".trimIndent()

    private fun generateChangelog(): String = """
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

    <!-- Add changesets for the $moduleName module here -->

</databaseChangeLog>
""".trimIndent()
}
