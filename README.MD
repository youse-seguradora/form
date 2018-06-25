FORM
========

RxJava form validation for Android and Java by Youse Seguros.

Sample with Kotlin and RxBinding
--------
Just create a `RxForm.Builder<T>()` where `T` is the type of each observableValue id,
then you can call `addFieldValidations()` for each observableValue with its id, an observable that listens to changes in the given observableValue and the observableValue validators.
After that, call the `build()` method and use the `RxForm` methods to listen to
changes in the validation of each observableValue (`onFieldValidationChange)`) or in the form as a whole (`onFormValidationChange()`).

```
val form = RxForm.Builder<Int>(submit.clicks())
                .addFieldValidations(emailContainer.id,
                        email.textChanges(), emailValidations)
                .addFieldValidations(passwordContainer.id,
                        password.textChanges(),
                        passwordValidations)
                .build()

        disposables.add(form.onFieldValidationChange()
                .subscribe {
                    val observableValue = findViewById<TextInputLayout>(it.first)
                    observableValue.isErrorEnabled = it.second.isNotEmpty()
                    observableValue.error = it.second.joinToString { it.message }
                })
        disposables.add(form.onFormValidationChange()
                .subscribe {
                    submit.isEnabled = it
                })
```

VALIDATION
--------
As an optional dependency we offer the possibility to use our validators module.

A form validation is related to each observableValue validation,
so we need to define a list of validations for each observableValue in the screen.
You can define your own validators by implementing the `Validator<T>` interface,
in the sample below we define an `HoursValidator` and a `ValidationMessage`, it takes a `message` to be shown
to the user when the validation fails and a `ValidationType` that was created to
specify which type of validation failed (you can have multiple validators with the same validation type).

Note that your form can validate any type of input, here we used `String`,
but your implementation can be of any class that you define and implements the `isValid` method.

```
class HoursValidator(val message: String, val splitter: String) : Validator<String> {
    override fun validationMessage(): ValidationMessage {
        return ValidationMessage(message = message, validationType = HOUR_FORMAT)
    }

    override fun isValid(input: String): Boolean {
        val parts = input.split(splitter)
        val hours = parts.firstOrNull()?.toInt() ?: -1
        val minutes = parts.lastOrNull()?.toInt() ?: -1
        return parts.size == 2
                && hours >= 0
                && hours <= 23
                && minutes >= 0
                && minutes <= 59
    }
}
```

FORMATTERS
--------
As an optional dependency we offer the possibility to use our formatters module.
Usually a formatter is coupled with the observableValue validation. A observableValue that displays hour and minutes (HH:MM) could be valid
if the the hours and minutes are valid only if in a given format.
So a given formatter is used together with a given validator.

```
// 00:00
class HoursFormatter(val splitter: String) : TextFormatter {

    override fun getCursorPosition(previous: String, input: String, output: String) = output.length

    override fun format(inputText: String): String {
        val clearText = inputText.toDigitsOnly()

        return when (clearText.length) {
            0, 1, 2 -> clearText
            3, 4 -> clearText.substring(0, 2) + splitter + clearText.substring(2)
            else -> clearText.substring(0, 2) + splitter + clearText.substring(2, 4)
        }
    }

}

private fun String.toDigitsOnly(): String {
    return this.replace(Pattern.compile("[^0-9]").toRegex(), "")
}
```
Note that each implementation of `TextFormatter` has a `getCursorPosition()` method that should return
where the observableValue cursor should be positioned after each change in the input observableValue. A more general interface `Formatter<T>` can be used
when the cursor position is not important or possible to be implemented.

Download
--------

Download or grab via Maven:
```xml
<dependency>
  <groupId>br.com.youse.form</groupId>
  <artifactId>form</artifactId>
  <version>0.0.1</version>
</dependency>
```
or Gradle:
```groovy
implementation 'br.com.youse.form:form:0.0.1'
implementation 'br.com.youse.form:validators:0.0.1' // optional
implementation 'br.com.youse.form:formatters:0.0.1' // optional
```


