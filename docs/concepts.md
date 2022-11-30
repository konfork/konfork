# Concepts

## Validator

## Validator Builder

## Constraint Builder

```kotlin
fun Specification<Unit, String, String>.sandyPlanet() =
    addConstraint("must be a sandy planet") {
        it == "Tatooine" || it == "Arrakis"
    }
```
