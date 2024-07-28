# Properties manager

Gradle plugin to load and bind project properties, simplifying property management in your project.
You can load properties manually or automatically bind property keys to `Property<T>` instances.  

Essentially, you can load properties in two ways:
- Using project properties (using -P option). e.g., `./gradlew myTask -PmyProperty=HelloWorld!`.
- Using gradle.properties file.

> **_NOTE:_**  Project properties take precedence over those defined in gradle.properties

# Index
- [Installation](#installation)
- [Load properties manually](#load-properties-manually)
- [Bind properties](#bind-properties)
  - [Bind single value properties](#bind-single-value-properties)
  - [Bind multi value properties](#bind-multi-value-properties)
  - [Bind custom property types](#bind-custom-properties)
- [Compatibility](#compatibility)

## Installation
To use the plugin in your project, you need to add a single line in your plugins section.  

For projects using Kotlin DSL:
```kotlin
plugins {
    id("com.cquilezg.properties-manager") version ("1.0")
}
```

For projects using Groovy DSL:
```groovy
plugins {
    id 'com.cquilezg.properties-manager' version '1.0'
}
```

The plugin gives you the `propertyManager` extension that provides some useful methods to load and bind project properties.

## Load properties manually
To load properties you can use the following methods:

Kotlin DSL:
```kotlin
tasks.register("helloWorld") {
    println(propertyManager.loadStringProperty(project, "myStringProperty"))  // Loads String property
    println(propertyManager.loadBooleanProperty(project, "myBooleanProperty"))  // Loads Boolean property
}
```

Groovy DSL:
```groovy
task.register("helloWorld") {
    println propertyManager.loadStringProperty(project, "myStringProperty")  // Loads String property
    println propertyManager.loadBooleanProperty(project, "myBooleanProperty")  // Loads Boolean property
}
```

After that, you can test it:  
```shell
./gradlew helloWorld -PmyStringProperty="Hello World!" -PmyBooleanProperty=TRUE
```

Output:  
```text
Hello World!
true
```

You can also use gradle.properties to set default property values.  
gradle.properties:  
`myStringProperty=Hi!`

Run:  
```shell
./gradlew helloWorld
```

And the output is:
```text
Hi!
null
```

## Bind properties

### Bind single value properties
Properties manager allows you to link standard property types to existing `Property<T>` instances. Here’s how it works:

1. Property Binding: You can specify a property key (e.g., my.string.property), and our plugin will look for this key in the property manager.
2. Value Assignment: If the property key exists, its value will be assigned to the corresponding `Property<T>` instance you've provided.

In essence, this feature automatically updates your `Property<T>` instances with values from predefined property keys, simplifying property management in your Gradle build.

Kotlin DSL:
```kotlin
tasks.register("bindProperties") {
    val stringProperty = project.objects.property(String::class.java)
    propertyManager.bindStringProperty(project, stringProperty, "my.string.property")
    println("My String property: ${stringProperty.orNull}")

    val intProperty = project.objects.property(Int::class.java)
    propertyManager.bindIntProperty(project, intProperty, "my.int.property")
    println("My Int property: ${intProperty.orNull}")
}
```

Groovy DSL:
```groovy
tasks.register("bindProperties") {
    Property<String> stringProperty = project.objects.property(String)
    propertyManager.bindStringProperty(project, stringProperty, "my.string.property")
    println "My String property: ${stringProperty.orNull}"

    Property<Integer> intProperty = project.objects.property(Integer)
    propertyManager.bindIntProperty(project, intProperty, "my.int.property")
    println "My Int property: ${intProperty.orNull}"
}
```

Run:  
```shell
./gradlew bindProperties -Pmy.string.property="An example" -Pmy.int.property=75
```

Output:
```text
My String property: An example
My Int property: 75
```

Supported types:
- String: `propertyManager.bindStringProperty(project, stringProperty, "my.string.property")`
- Char: `propertyManager.bindCharProperty(project, charProperty, "my.char.property")`
- Int: `propertyManager.bindIntProperty(project, intProperty, "my.int.property")`
- Long: `propertyManager.bindLongProperty(project, longProperty, "my.long.property")`
- Float: `propertyManager.bindFloatProperty(project, floatProperty, "my.float.property")`
- Double: `propertyManager.bindDoubleProperty(project, doubleProperty, "my.double.Property")`
- Boolean: `propertyManager.bindBooleanProperty(project, booleanProperty, "my.boolean.Property")`
- BigInteger: `propertyManager.bindBigIntegerProperty(project, bigIntegerProperty, "my.big.integer.property")`
- BigDecimal: `propertyManager.bindBigDecimalProperty(project, bigDecimalProperty, "my.big.decimal.property")`

### Bind multi value properties
You can bind properties with comma separated values to an existing `SetProperty<T>` or `ListProperty<T>` instance. Here some examples:

Kotlin DSL:
```kotlin
tasks.register("bindMultiValueProperties") {
    val setCharProperty = project.objects.setProperty(Char::class.java)
    propertyManager.bindMultiValueProperty(project, setCharProperty, "setChar", Char::class.java)
    println("Set Char: ${setCharProperty.orNull}")
}
```

Groovy DSL:
```groovy
tasks.register("bindMultiValueProperties") {
    SetProperty<Character> setCharProperty = project.objects.setProperty(Character)
    propertyManager.bindMultiValueProperty(project, setCharProperty, "setChar", Character)
    println("Set Char: " + setCharProperty.getOrNull())
}
```

Run:  
```shell
./gradlew bindMultiValueProperties -PsetChar=A,B,Z
```

Output:
```text
Set Char: [A, B, Z]
```

#### Supported Collections
- ListProperty
- SetProperty

#### Collection supported types

Kotlin DSL:
- String: `propertyManager.bindMultiValueProperty(project, setOrListStringProperty, "my.setOrListString.property", String::class.java)`
- Char: `propertyManager.bindMultiValueProperty(project, setOrListCharProperty, "my.setOrListChar.property", Char::class.java)` //
- Int: `propertyManager.bindMultiValueProperty(project, setOrListIntProperty, "my.int.property", Int::class.java)`
- Long: `propertyManager.bindMultiValueProperty(project, setOrListLongProperty, "my.long.property", Long::class.java)`
- Float: `propertyManager.bindMultiValueProperty(project, setOrListFloatProperty, "my.float.property", Float::class.java)`
- Double: `propertyManager.bindMultiValueProperty(project, setOrListDoubleProperty, "my.double.Property", Double::class.java)`
- Boolean: `propertyManager.bindMultiValueProperty(project, setOrListBooleanProperty, "my.boolean.Property", Boolean::class.java)`
- BigInteger: `propertyManager.bindMultiValueProperty(project, setOrListBigIntegerProperty, "my.big.integer.property", BigInteger::class.java)`
- BigDecimal: `propertyManager.bindMultiValueProperty(project, setOrListBigDecimalProperty, "my.big.decimal.property", BigDecimal::class.java)`

Groovy DSL:
- String: `propertyManager.bindMultiValueProperty(project, setOrListStringProperty, "my.setOrListString.property", String)`
- Char: `propertyManager.bindMultiValueProperty(project, setOrListCharProperty, "my.setOrListChar.property", Character)` //
- Int: `propertyManager.bindMultiValueProperty(project, setOrListIntProperty, "my.int.property", Integer)`
- Long: `propertyManager.bindMultiValueProperty(project, setOrListLongProperty, "my.long.property", Long)`
- Float: `propertyManager.bindMultiValueProperty(project, setOrListFloatProperty, "my.float.property", Float)`
- Double: `propertyManager.bindMultiValueProperty(project, setOrListDoubleProperty, "my.double.Property", Double)`
- Boolean: `propertyManager.bindMultiValueProperty(project, setOrListBooleanProperty, "my.boolean.Property", Boolean)`
- BigInteger: `propertyManager.bindMultiValueProperty(project, setOrListBigIntegerProperty, "my.big.integer.property", BigInteger)`
- BigDecimal: `propertyManager.bindMultiValueProperty(project, setOrListBigDecimalProperty, "my.big.decimal.property", BigDecimal)`

### Bind custom properties
You can also bind properties in custom types. Here an example to load Charsets.

Kotlin DSL:
```kotlin
tasks.register("customProperty") {
    val charsetProperty = project.objects.property(Charset::class.java)  // Create a Property<Charset!> or use an existing property. Depending on your needs.
    propertyManager.bindCustomProperty(project, charsetProperty, "custom.charset", Charset::class.java) { charsetConverter(it) } // Binds the property specifying a converting function
    println("My charset: $charsetProperty")
}

fun charsetConverter(charsetName: String): Charset {
    return Charset.forName(charsetName)
}
```

Groovy DSL:
```groovy
task.register("customProperty") {
    Property<Charset> charsetProperty = project.objects.property(Charset)   // Create a Property<Charset> or use an existing property. Depending on your needs.
    propertyManager.bindCustomProperty(project, charsetProperty, "custom.charset", Charset, this::charsetConverter) // Binds the property specifying a converting function
    println("Input charset: " + charsetProperty)
}

Charset charsetConverter(String charsetName) {
    return Charset.forName(charsetName)
}
```

When you run:  
```shell
./gradlew customProperty -Pcustom.charset=ISO-8859-1
```

The output is:
```text
My charset: property(java.nio.charset.Charset, property(java.nio.charset.Charset, fixed(class sun.nio.cs.ISO_8859_1, ISO-8859-1)))
```

## Compatibility
This plugin has been tested with Groovy and Kotlin versions of Gradle in the next releases:
- 5.6.4
- 6.9.3
- 7.6.2
- 8.9
