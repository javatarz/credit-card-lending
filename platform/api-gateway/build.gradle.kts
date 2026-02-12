plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.avast.gradle.docker-compose") version "0.17.12"
}

val springdocVersion: String by project
val postgresqlVersion: String by project
val liquibaseVersion: String by project

dependencies {
    implementation(project(":shared:kernel"))
    implementation(project(":shared:infrastructure"))
    implementation(project(":shared:events"))

    // Feature modules
    implementation(project(":modules:customer"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // OpenAPI / Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    // Database
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

springBoot {
    mainClass.set("me.karun.bank.credit.gateway.Application")
}

dockerCompose {
    useComposeFiles.add("../../docker-compose.yml")
    waitForTcpPorts.set(true)
    stopContainers.set(false) // Keep containers running after task completes
    removeVolumes.set(false) // Preserve database data on composeDown
}

// composeDown (plugin-generated) only stops containers it started in the same session.
// Since stopContainers=false, containers outlive the Gradle process, making composeDown a no-op.
// Override to always use forced behavior so it reliably stops containers.
tasks.named("composeDown") {
    actions.clear()
    dependsOn("composeDownForced")
}

tasks.named("bootRun") {
    dependsOn("composeUp")
}
