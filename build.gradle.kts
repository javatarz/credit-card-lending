plugins {
    java
    id("org.springframework.boot") version "4.0.0-RC1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "7.0.4"
}

allprojects {
    group = "me.karun.bank.credit"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    spotless {
        java {
            googleJavaFormat("1.35.0")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-parameters"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// Module scaffolding task
tasks.register<CreateModuleTask>("createModule") {
    moduleName = project.findProperty("moduleName")?.toString() ?: ""
}

// Create modules directory if it doesn't exist
tasks.register("initModulesDir") {
    doLast {
        file("modules").mkdirs()
    }
}

tasks.named("createModule") {
    dependsOn("initModulesDir")
}
