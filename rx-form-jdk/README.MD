RxForm
========

Reactive version of `Form`

How to `RxForm`
--------
1) Create a `RxForm.Builder<T>(submitObservable)` where `T` is the type of each field key and `submitObservable` and Observable that emits at every submit event;
2) Call `addField()` as many fields you would like to have in form;
3) Execute the `build()` method to get a hold of a `IRxForm` instance;
4) Subscribe to the Observables available from the `IRxForm` instance methods;


``` kotlin
val submitObservable = submit.clicks()
val emailChanges = email.textChanges().map { it.toString() }
val passwordChanges = password.textChanges().map { it.toString() }

val emailField = RxField(emailContainer.id, emailChanges, emailValidations)
val passwordField = RxField(passwordContainer.id, passwordChanges, passwordValidations)

form = RxForm.Builder<Int>(submitObservable)
        .addField(emailField)
        .addField(passwordField)
        .build()

disposables.add(form.onFieldValidationChange()
        .subscribe { pair ->
            findViewById<TextInputLayout>(pair.first)
                .error = pair.second.joinToString { it.message }
        })

disposables.add(form.onFormValidationChange()
        .subscribe {
            submit.isEnabled = it
        })
```

Full sample [here](https://github.com/youse-seguradora/form/blob/master/app/src/main/kotlin/br/com/youse/forms/samples/login/rx/RxLoginActivity.kt)

RxField<T,R> properties
--------
## RxField.key`<T>`
 - It's an ID to identify uniquely the field in a form. Of type `T`.

## RxField.input`<R>`
 - It's an `Observable<R>` used to feed the field value change events to the form.

## RxField.errors`List<ValidationMessage`
 - It's an `Observable<List<ValidationMessage>>` where you subscribe to receive this field validation change events.

## RxField.enabled`<Boolean>`
 - It's an `Observable<Boolean>` used to feed the field status change events to the form.
 - Emitting a `True` will cause a field validation if the form strategy allows.
 - Emitting a `False` will cause the `FormField.errors` to be called with an empty List of validations if the form validation allows.
 - Emitting a `False` will remove this field from the form submission data.

## RxField.validators`<R>`
  - It's a list of `Validator<R>`.

## RxField.validationTriggers
 - It's a list of `Observable<Unit>` used to force a round of validation to happen to this field.


Download
--------

Download or grab via Maven:
```xml
<dependency>
  <groupId>br.com.youse.forms</groupId>
  <artifactId>rx-form-jdk</artifactId>
  <version>0.5.0</version>
</dependency>
```
or Gradle:
add
```groovy
        maven { url 'https://oss.sonatype.org/content/groups/public' }
```
and
```groovy
implementation 'br.com.youse.forms:rx-form-jdk:0.5.0'
```


