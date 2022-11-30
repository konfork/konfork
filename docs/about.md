# About

Konfork is a fork of Konform. While Konform is an excellent project there were some important features missing:

- Custom error types
- Validation context
- Validator builders like `lazy`, `eager` and `conditional`
- Custom hints on `required`

Besides this, the design philosophy was to not add a series of default validations (e.g. `uuid()` or `email()`).

This projects aim is to add these features and add many default validations, ready to be used.
