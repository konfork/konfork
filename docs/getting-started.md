# Getting started

Note that this is a newly forked project. Because of this I expect the API to be unstable the first couple of versions.

## Setup

For multiplatform projects:

```
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.konfork:konfork-core:0.0.4")
            }
        }
    }
}
```

For jvm-only projects add:

```
dependencies {
    implementation("io.github.konfork:konfork-core-jvm:0.0.4")
}
```

## Writing your first Validator

Most validators don't need any advanced features. 

Given a simple data class like

```kotlin
data class UserProfile(
    val fullName: String,
    val age: Int?
)
```

A possible validator can be as simple as:

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
```

## Using a validator

To run the validator:
```kotlin
val invalidUser = UserProfile("A", -1)
val result = validateUser(invalidUser)
// result holds two errors: "must have at least 2 characters" and "must be at least '0'"
```

The result can be used in several ways (see arrow module for pretty patterns):
```kotlin
when (result) {
    is Valid -> result.value // value holds the user
    is Invalid -> result.errors // Get a list of all errors
}
```
