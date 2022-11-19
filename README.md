[![Test](https://github.com/konfork/konfork/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/konfork/konfork/actions/workflows/gradle.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.konfork/konfork-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.konfork/konfork-core)

# Declarative Validators for Kotlin

How often have you written validations on untrusted data? The bulk of these validations were pretty simple, but
repetitive; `if (!condition) { create error }`. This pattern gets tiresome really quickly. Things get
worse if you want to *eagerly* validate many fields.

No more!

Using Konfork's declarative style, you can simply write things like:

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

## Usage

Note that this is a newly forked project. Because of this I expect the API to be unstable the first couple of versions.

### Setup

For multiplatform projects:

```
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.konfork:konfork-core:0.0.2")
            }
        }
    }
}
```

For jvm-only projects add:

```
dependencies {
    implementation("io.github.konfork:konfork-core-jvm:0.0.2")
}
```


## About
Konfork is a fork of [Konform](https://www.konform.io). While Konform is an excellent project there were two big
features missing:

- Custom error types
- Validation context

Besides this, the design philosophy was to not add a series of default validations (e.g. `isUuid()` or `isEmail()`).

This projects aim is to add these features.

## TODO

- [ ] Add multipleOf validations for Long and Int using modulo
- [x] Add validations: isEmail, checkdigit (EAN, mod10, mod11, luhn, ISBN)
- [ ] Add validation: Verhoeff
- [ ] Add validation: URL
- [ ] Add validation: isDate
- [x] Add validation: lengthIn (range)
- [ ] Add Number validations: inRange
- [x] Add separate module with password strength validation nbvcxz
- [ ] Add separate module for utils for interoperability with Arrow
- [ ] Add conditional validators
- [ ] Make infix from has and add is(?)
- [ ] Add oneOf validator

##### License

[MIT License](https://github.com/konfork/konfork/blob/master/LICENSE)
