package com.cquilez

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

open class PropertyManagerExtension {
    fun bindProperty(project: Project, setProperty: SetProperty<String>, propertyName: String) {
        loadSetProperty(project, propertyName)?.let { setProperty.value(it) }
    }

    fun bindStringProperty(project: Project, stringProperty: Property<String>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { stringProperty.value(it) }
    }

    fun bindBooleanProperty(project: Project, booleanProperty: Property<Boolean>, propertyName: String) {
        loadBooleanProperty(project, propertyName)?.let { booleanProperty.value(it) }
    }

    fun loadSetProperty(project: Project, propertyName: String): SetProperty<String>? {
        val propertyValue = project.properties[propertyName]
        return if (propertyValue != null && (propertyValue as String).isNotBlank()) {
            project.objects.setProperty(String::class.java).value(propertyValue.split(","))
        } else
            null
    }

    fun loadStringProperty(project: Project, propertyName: String): String? {
        val propertyValue = project.properties[propertyName]
        return if (propertyValue != null && (propertyValue as String).isNotBlank()) {
            propertyValue
        } else
            null
    }

    fun loadBooleanProperty(project: Project, propertyName: String): Boolean? {
        val propertyValue = project.properties[propertyName]
        if (propertyValue is String) {
            return propertyValue.equals("true", true)
        }
        return null
    }
}