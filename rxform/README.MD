RXFORM
========

Reactive version of `Form`

How to `RxForm`
--------
1) Create a `RxForm.Builder<T>(submitObservable)` where `T` is the type of each field key and `submitObservable` and Observable that emits at every submit event;
2) Call `addFieldValidations()` as many fields in the screen using as parameters a key, an `Observable` and a list of validators;
3) Execute the `build()` method to get a hold of a `RxForm` instance;
4) Subscribe to the Observables available from the `RxForm` instance methods;


``` kotlin
val form = RxForm.Builder<Int>(submit.clicks())
        .addFieldValidations(emailContainer.id,
                email.textChanges(), emailValidations)
        .addFieldValidations(passwordContainer.id,
                password.textChanges(),
                passwordValidations)
        .build()

disposables.add(form.onFieldValidationChange()
        .subscribe {
            val field = findViewById<TextInputLayout>(it.first)
            field.isErrorEnabled = it.second.isNotEmpty()
            field.error = it.second.joinToString { it.message }
        })
disposables.add(form.onFormValidationChange()
        .subscribe {
            submit.isEnabled = it
        })
```

Full sample [here](https://github.com/youse-seguradora/form/blob/master/app/src/main/kotlin/br/com/youse/forms/rx/RxLoginActivity.kt)

Download
--------

Download or grab via Maven:
```xml
<dependency>
  <groupId>br.com.youse.form</groupId>
  <artifactId>rxform</artifactId>
  <version>0.0.1</version>
</dependency>
```
or Gradle:
```groovy
implementation 'br.com.youse.form:rxform:0.0.1'
implementation 'br.com.youse.form:validators:0.0.1' // optional
implementation 'br.com.youse.form:formatters:0.0.1' // optional
```


