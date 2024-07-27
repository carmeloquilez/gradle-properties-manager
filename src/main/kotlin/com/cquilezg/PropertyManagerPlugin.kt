package com.cquilezg

import org.gradle.api.Plugin
import org.gradle.api.Project

class PropertyManagerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("propertyManager", PropertyManagerExtension::class.java)
    }
}