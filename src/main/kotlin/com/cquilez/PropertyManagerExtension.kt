package com.cquilez

import org.gradle.api.Project
import org.gradle.api.provider.HasMultipleValues
import org.gradle.api.provider.Property
import java.math.BigDecimal
import java.math.BigInteger

open class PropertyManagerExtension {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> bindMultiValueProperty(project: Project, setProperty: HasMultipleValues<T>, propertyName: String, type: Class<T>) {
        val propertyValue = project.properties[propertyName]
        if (propertyValue != null && (propertyValue as String).isNotBlank()) {
            val stringList = propertyValue.split(",")
            val tList: List<T> = when (type) {
                String::class.java -> {
                    stringList as List<T>
                }

                Char::class.java, Character::class.java -> {
                    stringList.mapTo(mutableListOf()) {
                        if (it.length == 1 ) {
                            it[0] as T
                        } else {
                            throw IllegalArgumentException("Error loading Char property: $propertyName. Value: '$it' cannot be converted to Char.")
                        }
                    }
                }

                Int::class.java -> {
                    stringList.mapTo(mutableListOf()) { it.toInt() as T }
                }

                Long::class.java -> {
                    stringList.mapTo(mutableListOf()) { it.toLong() as T }
                }

                Float::class.java -> {
                    stringList.mapTo(mutableListOf()) { it.toFloat() as T }
                }

                Double::class.java -> {
                    stringList.mapTo(mutableListOf()) { it.toDouble() as T }
                }

                Boolean::class.java -> {
                    stringList.mapTo(mutableListOf()) { it.toBoolean() as T }
                }

                BigInteger::class.java -> {
                    stringList.mapTo(mutableListOf()) { it.toBigInteger() as T }
                }

                BigDecimal::class.java -> {
                    stringList.mapTo(mutableListOf()) { it.toBigDecimal() as T }
                }

                else -> {
                    throw IllegalArgumentException("Unknown class type: ${type.name}. If you use a custom class type, please provide a converter. Default supported types: String, Char, Int, Long, " +
                            "Float, Double, Boolean, BigInteger and Big Decimal.")
                }
            }
            val setProp = project.objects.setProperty(type).value(tList)
            setProperty.value(setProp)
        }
    }

    fun <T : Any> bindCustomProperty(project: Project, property: Property<T>, propertyName: String, type: Class<T>, converter: (String) -> T) {
        val propertyValue = project.properties[propertyName]
        if (propertyValue != null && (propertyValue as String).isNotBlank()) {
            val setProp = project.objects.property(type).value(converter.invoke(propertyValue))
            property.value(setProp)
        }
    }

    fun bindStringProperty(project: Project, stringProperty: Property<String>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { stringProperty.value(it) }
    }

    fun bindCharProperty(project: Project, charProperty: Property<Char>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let {
            if (it.length == 1 ) {
                charProperty.value(it[0])
            } else {
                throw IllegalArgumentException("Error loading Char property: $propertyName. Value: '$it' cannot be converted to Char.")
            }
        }
    }

    fun bindIntProperty(project: Project, intProperty: Property<Int>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { intProperty.value(it.toInt()) }
    }

    fun bindLongProperty(project: Project, longProperty: Property<Long>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { longProperty.value(it.toLong()) }
    }

    fun bindFloatProperty(project: Project, floatProperty: Property<Float>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { floatProperty.value(it.toFloat()) }
    }

    fun bindDoubleProperty(project: Project, doubleProperty: Property<Double>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { doubleProperty.value(it.toDouble()) }
    }

    fun bindBigIntegerProperty(project: Project, bigIntegerProperty: Property<BigInteger>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { bigIntegerProperty.value(it.toBigInteger()) }
    }

    fun bindBigDecimalProperty(project: Project, bigDecimalProperty: Property<BigDecimal>, propertyName: String) {
        loadStringProperty(project, propertyName)?.let { bigDecimalProperty.value(it.toBigDecimal()) }
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
}

