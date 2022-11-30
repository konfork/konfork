# Konfork

Declarative validations for Kotlin

## Validators

Konfork allows you to easily build and maintain validators:

```kotlin
val validateUser = Validator<UserProfile> {
    UserProfile::fullName {
        minLength(2)
        maxLength(100)
    }

    UserProfile::age ifPresent {
        minimum(0)
        maximum(150)
    }
}

val result = validateUser(someUser)
```
