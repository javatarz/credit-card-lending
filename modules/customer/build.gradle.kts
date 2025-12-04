plugins {
    `java-library`
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

val springBootVersion: String by project
val springdocVersion: String by project

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

dependencies {
    api(project(":shared:kernel"))
    api(project(":shared:events"))
    implementation(project(":shared:infrastructure"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Password hashing
    implementation("org.springframework.security:spring-security-crypto")

    // OpenAPI / Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}