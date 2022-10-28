[![Test](https://github.com/konfork/konfork/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/konfork/konfork/actions/workflows/gradle.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.konfork/konfork-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.konfork/konfork-core)

# Declarative Validations for Kotlin

How often have you written validations on untrusted data? The bulk of these validations were pretty simple, but
repetitive; `if (!condition) { create error }`. This pattern gets tiresome really quickly. Things get
worse if you want to *eagerly* validate many fields.

No more!

Using Konfork's declarative style, you can simply write things like:

```kotlin
val validateUser = Validation<UserProfile> {
    UserProfile::fullName {
        minLength(2)
        maxLength(100)
    }

    UserProfile::age ifPresent {
        minimum(0)
        maximum(150)
    }
}
```
