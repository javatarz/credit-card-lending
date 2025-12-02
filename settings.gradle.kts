rootProject.name = "credit-card-lending"

pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings

    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
    }
}

// Shared modules
include("shared:kernel")
include("shared:infrastructure")
include("shared:events")

// Platform modules
include("platform:api-gateway")

// Feature modules - auto-discovered from modules/ directory
file("modules").listFiles()?.filter { it.isDirectory && file("${it.path}/build.gradle.kts").exists() }?.forEach {
    include("modules:${it.name}")
}
