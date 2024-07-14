package com.cquilez

import org.gradle.api.Project
import org.gradle.api.provider.HasMultipleValues
import org.gradle.api.provider.Property
import java.math.BigDecimal

open class PropertyManagerExtension {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> bindMultiValueProperty(project: Project, setProperty: HasMultipleValues<T>, propertyName: String, t: Class<T>) {
        val propertyValue = project.properties[propertyName]
        if (propertyValue != null && (propertyValue as String).isNotBlank()) {
            val stringList = propertyValue.split(",")
            val tList: List<T> = when (t) {
                Int::class.java -> {
                    stringList.mapTo(mutableListOf(), this::convertToInt)
                }

                String::class.java -> {
                    stringList as List<T>
                }

                else -> {
                    throw IllegalArgumentException("Unknown class type: ${t.name}. If you use a custom class type, please provide a converter. Default known types: String, Int, Boolean.")
                }
            }
            val setProp = project.objects.setProperty(t).value(tList)
            setProperty.value(setProp)
        }
    }

    fun bindStringProperty(project: Project, stringProperty: Property<String>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { stringProperty.value(it) }
    }

    fun bindIntProperty(project: Project, stringProperty: Property<Int>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { stringProperty.value(it.toInt()) }
    }

    fun bindLongProperty(project: Project, stringProperty: Property<Long>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { stringProperty.value(it.toLong()) }
    }

    fun bindFloatProperty(project: Project, stringProperty: Property<Float>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { stringProperty.value(it.toFloat()) }
    }

    fun bindDoubleProperty(project: Project, stringProperty: Property<Double>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { stringProperty.value(it.toDouble()) }
    }

    fun bindBigDecimalProperty(project: Project, stringProperty: Property<BigDecimal>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { stringProperty.value(it.toBigDecimal()) }
    }

    fun bindBooleanProperty(project: Project, booleanProperty: Property<Boolean>, propertyName: String) {
        loadBooleanProperty(project, propertyName)?.let { booleanProperty.value(it) }
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

    @Suppress("UNCHECKED_CAST")
    private fun <T> convertToInt(string: String) = string.toInt() as T
}

