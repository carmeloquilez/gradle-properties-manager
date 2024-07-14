package com.cquilez

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class LoadPropertyValuesFunctionalTest {
    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File
    private lateinit var gradleProperties: File

    @BeforeEach
    fun setup() {
        settingsFile = File(testProjectDir, "settings.gradle")
        writeFile(settingsFile, "rootProject.name = 'properties-manager-test'")
        buildFile = File(testProjectDir, "build.gradle")
        val buildFileContent = """
            plugins {
                id 'com.cquilez.gradle-properties-manager'
            }
            task testStringProperty {
                doLast {
                    println 'Property value: ' + propertyManager.loadStringProperty(project, "my.string.property")
                }
            }
        """.trimIndent()
        writeFile(buildFile, buildFileContent)
    }

    @ParameterizedTest
    @MethodSource("gradleVersions")
    fun `no property loaded as there is no gradle properties and no project property written`(gradleVersion: String) {
        val result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir)
            .withArguments("testStringProperty")
            .withPluginClasspath()
            .build()

        assertThat(result.output, containsString("Property value: null"))
        Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":testStringProperty")!!.outcome)
    }

    @ParameterizedTest
    @MethodSource("gradleVersions")
    fun `load string property from gradle properties file`(gradleVersion: String) {
        val gradlePropertiesContent = """
            my.string.property="Hello world!"
        """.trimIndent()
        gradleProperties = File(testProjectDir, "gradle.properties")
        writeFile(gradleProperties, gradlePropertiesContent)

        val result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir)
            .withArguments("testStringProperty")
            .withPluginClasspath()
            .build()

        assertThat(result.output, containsString("Hello world!"))
        Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":testStringProperty")!!.outcome)
    }

    @ParameterizedTest
    @MethodSource("gradleVersions")
    fun `load string property from project properties`(gradleVersion: String) {
        val result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir)
            .withArguments("testStringProperty", "-Pmy.string.property=HelloWorld!")
            .withPluginClasspath()
            .build()

        assertThat(result.output, containsString("HelloWorld!"))
        Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":testStringProperty")!!.outcome)
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

    companion object {
        @JvmStatic
        fun gradleVersions() = listOf(
            Arguments.of("5.6.4"),
            Arguments.of("6.9.3"),
            Arguments.of("7.6.2"),
            Arguments.of("8.9")
        )
    }
}