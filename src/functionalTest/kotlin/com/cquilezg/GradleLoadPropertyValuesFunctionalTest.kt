package com.cquilezg

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class GradleLoadPropertyValuesFunctionalTest {
    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File
    private lateinit var gradleProperties: File

    @Nested
    inner class LoadStringTest {
        private val taskName = "testStringProperty"

        @BeforeEach
        fun setup() {
            settingsFile = File(testProjectDir, "settings.gradle")
            writeFile(settingsFile, "rootProject.name = 'properties-manager-test'")
            buildFile = File(testProjectDir, "build.gradle")
            val buildFileContent = """
            plugins {
                id 'com.cquilezg.properties-manager'
            }
            task $taskName {
                doLast {
                    println 'Property value: ' + propertyManager.loadStringProperty(project, "my.string.property")
                }
            }
        """.trimIndent()
            writeFile(buildFile, buildFileContent)
        }

        @ParameterizedTest
        @MethodSource(GradleVersionsSupplier.METHOD_REFERENCE)
        @DisplayName("If no gradle.properties present and no project properties, no property is loaded")
        fun noPropertyLoadedAsThereIsNoGradlePropertiesAndNoProjectPropertyWritten(gradleVersion: String) {
            val result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir)
                .withArguments(taskName)
                .withPluginClasspath()
                .build()

            assertThat(result.output, containsString("Property value: null"))
            Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")!!.outcome)
        }

        @ParameterizedTest
        @MethodSource(GradleVersionsSupplier.METHOD_REFERENCE)
        @DisplayName("If property is found at gradle.properties, loads it")
        fun loadStringPropertyFromGradlePropertiesFile(gradleVersion: String) {
            val gradlePropertiesContent = """
            my.string.property=Hello world!
        """.trimIndent()
            gradleProperties = File(testProjectDir, "gradle.properties")
            writeFile(gradleProperties, gradlePropertiesContent)

            val result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir)
                .withArguments(taskName)
                .withPluginClasspath()
                .build()

            assertThat(result.output, containsString("Property value: Hello world!"))
            Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")!!.outcome)
        }

        @ParameterizedTest
        @MethodSource(GradleVersionsSupplier.METHOD_REFERENCE)
        @DisplayName("If project property is provided, uses it")
        fun loadStringPropertyFromProjectProperties(gradleVersion: String) {
            val result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir)
                .withArguments(taskName, "-Pmy.string.property=HelloWorld!")
                .withPluginClasspath()
                .build()

            assertThat(result.output, containsString("Property value: HelloWorld!"))
            Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")!!.outcome)
        }

        @ParameterizedTest
        @MethodSource(GradleVersionsSupplier.METHOD_REFERENCE)
        @DisplayName("Project property overrides gradle.properties property")
        fun projectPropertyOverridesGradlePropertiesFileProperty(gradleVersion: String) {
            val gradlePropertiesContent = """
            my.string.property="Hello world!"
        """.trimIndent()
            gradleProperties = File(testProjectDir, "gradle.properties")
            writeFile(gradleProperties, gradlePropertiesContent)

            val result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir)
                .withArguments(taskName, "-Pmy.string.property=Hello...")
                .withPluginClasspath()
                .build()

            assertThat(result.output, containsString("Property value: Hello..."))
            Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")!!.outcome)
        }
    }

    @Nested
    inner class LoadBooleanTest {
        private val taskName = "testBooleanProperty"

        @BeforeEach
        fun setup() {
            settingsFile = File(testProjectDir, "settings.gradle")
            writeFile(settingsFile, "rootProject.name = 'properties-manager-test'")
            buildFile = File(testProjectDir, "build.gradle")
            val buildFileContent = """
            plugins {
                id 'com.cquilezg.properties-manager'
            }
            task $taskName {
                doLast {
                    println 'Property value: ' + propertyManager.loadBooleanProperty(project, "my.boolean.property")
                }
            }
        """.trimIndent()
            writeFile(buildFile, buildFileContent)
        }

        @ParameterizedTest
        @MethodSource(GradleVersionsSupplier.METHOD_REFERENCE)
        @DisplayName("If no gradle.properties present and no project properties, no property is loaded")
        fun noPropertyLoadedAsThereIsNoGradlePropertiesAndNoProjectPropertyWritten(gradleVersion: String) {
            val result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir)
                .withArguments(taskName)
                .withPluginClasspath()
                .build()

            assertThat(result.output, containsString("Property value: null"))
            Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")!!.outcome)
        }

        @ParameterizedTest
        @MethodSource(GradleVersionsSupplier.METHOD_REFERENCE)
        @DisplayName("If property is found at gradle.properties, loads it")
        fun loadStringPropertyFromGradlePropertiesFile(gradleVersion: String) {
            val gradlePropertiesContent = """
            my.boolean.property=true
        """.trimIndent()
            gradleProperties = File(testProjectDir, "gradle.properties")
            writeFile(gradleProperties, gradlePropertiesContent)

            val result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir)
                .withArguments(taskName)
                .withPluginClasspath()
                .build()

            assertThat(result.output, containsString("Property value: true"))
            Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")!!.outcome)
        }

        @ParameterizedTest
        @MethodSource(GradleVersionsSupplier.METHOD_REFERENCE)
        @DisplayName("If project property is provided, uses it")
        fun loadStringPropertyFromProjectProperties(gradleVersion: String) {
            val result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir)
                .withArguments(taskName, "-Pmy.boolean.property=true")
                .withPluginClasspath()
                .build()

            assertThat(result.output, containsString("Property value: true"))
            Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")!!.outcome)
        }

        @ParameterizedTest(name = "{index} => Gradle version={0}")
        @MethodSource(GradleVersionsSupplier.METHOD_REFERENCE)
        @DisplayName("Project property overrides gradle.properties property")
        fun projectPropertyOverridesGradlePropertiesFileProperty(gradleVersion: String) {
            val gradlePropertiesContent = """
            my.boolean.property=false
        """.trimIndent()
            gradleProperties = File(testProjectDir, "gradle.properties")
            writeFile(gradleProperties, gradlePropertiesContent)

            val result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir)
                .withArguments(taskName, "-Pmy.boolean.property=TRUE")
                .withPluginClasspath()
                .build()

            assertThat(result.output, containsString("Property value: true"))
            Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")!!.outcome)
        }
    }

    private fun writeFile(destination: File, content: String) {
        lateinit var output: BufferedWriter
        try {
            output = BufferedWriter(FileWriter(destination))
            output.write(content)
        } finally {
            output.close()
        }
    }
}