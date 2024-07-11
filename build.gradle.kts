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
    testClassesDirs = functionalTest.output.classesDirs
    classpath = functionalTest.runtimeClasspath
    useJUnitPlatform()
}

tasks.check {
    dependsOn(functionalTestTask)
}

gradlePlugin {
    plugins {
        create("gradle-properties-manager") {
            id = "com.cquilez.gradle-properties-manager"
            implementationClass = "com.cquilez.PropertyManagerPlugin"
        }
    }
    testSourceSets(functionalTest)
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(gradleTestKit())
    "functionalTestImplementation"("org.junit.jupiter:junit-jupiter:5.7.1")
    "functionalTestRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    "functionalTestRuntimeOnly"(gradleTestKit())
    "functionalTestImplementation"("org.hamcrest:hamcrest:2.1")
}

tasks.test {
    useJUnitPlatform()
}