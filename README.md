# properties-manager

Plugin to load and bind properties.  

You can load properties in two ways:
- Using project properties (using -P option). i.e. `./gradlew myTask -PmyProperty=HelloWorld!`
- gradle.properties

> **_NOTE:_**  Project properties take precedence over those defined in gradle.properties

## Compatibility
This plugin has been tested with Groovy and Kotlin versions of Gradle in the next releases:
- 5.6.4
- 6.9.3
- 7.6.2
- 8.9

## Load properties
To load properties you can use the following methods:

With Kotlin DSL:
```kotlin
tasks.register("helloWorld") {
    println(propertyManager.loadStringProperty(project, "myStringProperty"))  // Loads String property
    println(propertyManager.loadBooleanProperty(project, "myBooleanProperty"))  // Loads Boolean property
}
```

With Groovy DSL:
```groovy
task.register("helloWorld") {
    println propertyManager.loadStringProperty(project, "myStringProperty")  // Loads String property
    println propertyManager.loadBooleanProperty(project, "myBooleanProperty")  // Loads Boolean property
}
```

After that, you can test it:  
`./gradlew helloWorld -PmyStringProperty="Hello World!" -PmyBooleanProperty=TRUE`

Output:  
```
Hello World!
true
```

You can also use gradle.properties to set default property values.  
gradle.properties:  
`myStringProperty=Hi!`

Run:  
`./gradlew helloWorld`

And the output is:
```
Hi!
null
```

## Bind single value properties
You can bind common property types to an existing Property<T> instance. **The propertyManager extension will load for you the value if it is found**. Here some examples:

Kotlin DSL:
```kotlin
tasks.register("bindProperties") {
    val stringProperty = project.objects.property(String::class.java)
    propertyManager.bindStringProperty(project, stringProperty, "myStringProperty")
    println("My String property: ${stringProperty.orNull}")

    val intProperty = project.objects.property(Int::class.java)
    propertyManager.bindIntProperty(project, intProperty, "myIntProperty")
    println("My Int property: ${intProperty.orNull}")
}
```

Groovy DSL:
```groovy
tasks.register("bindProperties") {
    Property<String> stringProperty = project.objects.property(String)
    propertyManager.bindStringProperty(project, stringProperty, "myStringProperty")
    println "My String property: ${stringProperty.orNull}"

    Property<Integer> intProperty = project.objects.property(Integer)
    propertyManager.bindIntProperty(project, intProperty, "myIntProperty")
    println "My Int property: ${intProperty.orNull}"
}
```

Run:  
`./gradlew bindProperties -PmyStringProperty="An example" -PmyIntProperty=75`

Output:
```
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

## Bind multi value properties
You can bind multi value property types to an existing `SetProperty<T>` or `ListProperty<T>` instance. Here some examples:

Kotlin DSL:
```kotlin
tasks.register("bindMultiValueProperties") {
    val setCharProperty = project.objects.setProperty(Character::class.java)
    propertyManager.bindMultiValueProperty(project, setCharProperty, "setChar", Character::class.java)
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
`./gradlew bindMultiValueProperties -PsetChar=A,B,Z`

Output:
```
Set Char: [A, B, Z]
```

Supported Collections:
- ListProperty
- SetProperty

Supported types:
- String: `propertyManager.bindMultiValueProperty(project, setOrListStringProperty, "my.setOrListString.property", String::class.java)`
- Char: `propertyManager.bindMultiValueProperty(project, setOrListCharProperty, "my.setOrListChar.property", Character::class.java)` //
- Int: `propertyManager.bindMultiValueProperty(project, setOrListIntProperty, "my.int.property", Int::class.java)`
- Long: `propertyManager.bindMultiValueProperty(project, setOrListLongProperty, "my.long.property", Long::class.java)`
- Float: `propertyManager.bindMultiValueProperty(project, setOrListFloatProperty, "my.float.property", Float::class.java)`
- Double: `propertyManager.bindMultiValueProperty(project, setOrListDoubleProperty, "my.double.Property", Double::class.java)`
- Boolean: `propertyManager.bindMultiValueProperty(project, setOrListBooleanProperty, "my.boolean.Property", Boolean::class.java)`
- BigInteger: `propertyManager.bindMultiValueProperty(project, setOrListBigIntegerProperty, "my.big.integer.property", BigInteger::class.java)`
- BigDecimal: `propertyManager.bindMultiValueProperty(project, setOrListBigDecimalProperty, "my.big.decimal.property", BigDecimal::class.java)`

> **_NOTE:_**  The examples above are in Kotlin. For Groovy replace String::class.java by String, etc.)

### Bind custom properties
You can also bind properties in custom types. Here an example to load Charsets.

With Kotlin DSL:
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
`./gradlew customProperty -Pcustom.charset=ISO-8859-1`

The output is:  
```
My charset: property(java.nio.charset.Charset, property(java.nio.charset.Charset, fixed(class sun.nio.cs.ISO_8859_1, ISO-8859-1)))
```
