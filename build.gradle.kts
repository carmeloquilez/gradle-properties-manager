plugins {
    kotlin("jvm") version "1.9.23"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "com.cquilez"
version = "1.0-SNAPSHOT"

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
            id = "com.cquilez.properties-manager"
            implementationClass = "com.cquilez.PropertyManagerPlugin"
        }
    }
    testSourceSets(functionalTest)
}