plugins {
    `java-library`
}

val junitVersion: String by project

dependencies {
    api(project(":shared:kernel"))

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
