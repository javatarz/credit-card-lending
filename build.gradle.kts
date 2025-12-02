plugins {
    java
    id("org.springframework.boot") version "4.0.0-RC1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
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

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
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
