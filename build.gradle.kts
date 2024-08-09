plugins {
    kotlin("jvm") version "1.9.23"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
    signing
}

group = "com.cquilezg"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(8)
}

val functionalTest = sourceSets.create("functionalTest")
val functionalTestTask = tasks.register<Test>("functionalTest") {
    group = "verification"
    description = "Functional tests"
    testClassesDirs = functionalTest.output.classesDirs
    classpath = functionalTest.runtimeClasspath
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform()
}

tasks.check {
    dependsOn(functionalTestTask)
}

dependencies {
    testImplementation(libs.junitJupiter)
    testImplementation(gradleTestKit())
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    "functionalTestImplementation"(libs.junitJupiter)
    "functionalTestRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    "functionalTestRuntimeOnly"(gradleTestKit())
    "functionalTestImplementation"(libs.hamcrest)
}

gradlePlugin {
    plugins {
        create("properties-manager") {
            id = "io.github.cquilezg.properties-manager"
            implementationClass = "io.github.cquilezg.PropertyManagerPlugin"
            displayName = "Properties manager"
            description = """
                Load properties from command-line (using project properties: -P option) and from gradle.properties, handling primary property types
                like strings, numbers, booleans for flexible and dynamic configuration management
            """.trimIndent()
            tags = listOf("properties", "configuration", "project properties")
        }
    }
    website = "https://github.com/cquilezg/properties-manager"
    vcsUrl = "https://github.com/cquilezg/properties-manager.git"
    testSourceSets(functionalTest)
}