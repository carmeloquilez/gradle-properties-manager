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

class BuildLogicFunctionalTest {
    @TempDir
    lateinit var testProjectDir: File
    private lateinit var settingsFile: File
    private lateinit var buildFile: File
    private lateinit var gradleProperties: File

    @BeforeEach
    fun setup() {
        settingsFile = File(testProjectDir, "settings.gradle")
        buildFile = File(testProjectDir, "build.gradle")
        gradleProperties = File(testProjectDir, "gradle.properties")
    }

    @ParameterizedTest
    @MethodSource("versions")
    fun `load string property from gradle properties file`(gradleVersion: String) {
        writeFile(settingsFile, "rootProject.name = 'hello-world'")
        val buildFileContent = """
            plugins {
                id 'com.cquilez.gradle-properties-manager'
            }
            task helloWorld {
                doLast {
                    println propertyManager.loadStringProperty(project, "myProperty")
                }
            }
        """.trimIndent()
        writeFile(buildFile, buildFileContent)
        val gradlePropertiesContent = """
            myProperty=helloWorld!
        """.trimIndent()
        writeFile(gradleProperties, gradlePropertiesContent)

        val result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir)
            .withArguments("helloWorld")
            .withPluginClasspath()
            .build()

        assertThat(result.output, containsString("helloWorld!"))
        Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":helloWorld")!!.outcome)
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
        fun versions() = listOf(
            Arguments.of("5.6.4"),
            Arguments.of("6.9.3"),
            Arguments.of("7.6.2"),
            Arguments.of("8.9")
        )
    }
}