FORM
========

Form makes it easy to format and validate form fields and submit the form when every thing is ready.

And it come in many flavors:
 - Form for the 100% pure Kotlin library;
 - [RxForm](https://github.com/youse-seguradora/form/tree/master/rxform) for RxJava lovers;
 - [LiveDataForm](https://github.com/youse-seguradora/form/tree/master/livedata-form) for DataBinding/LiveData addicts;

How to Form
--------
1) Create a `Form.Builder<T>()` where `T` is the type of each field key;
2) Set the callbacks for the validation events, like `builder.setFieldValidationListener()` or `builder.setValidSubmitListener()`;
3) Call `builder.addFieldValidations()` as many fields in the form using the parameters:
   - a key of type T;
   - an `IForm.ObservableValue<R>` or `IForm.DeferredObservableValue<R>`;
   - a list of Validator<R>;
4) Execute the `builder.build()` method to get a hold of a `Form` instance;
5) Every time the user requests to submit the form, call the method `form.doSubmit()`;

In the sample below we use `Int` as `T` because we are using the Android R.id.$viewId `Int` as keys
and `String` as `R` since `email` and `password` are EditTexts.

``` kotlin
val emailChanges = ObservableValue(email.text.toString())
val passwordChanges = ObservableValue(password.text.toString())

email.addTextChangedListener(getTextWatcher(emailChanges))
password.addTextChangedListener(getTextWatcher(passwordChanges))


val form = Form.Builder<Int>()
        .setFieldValidationListener(this)
        .setFormValidationListener(this)
        .setValidSubmitListener(this)
        .setSubmitFailedListener(this)

        .addFieldValidations(emailContainer.id,
                emailChanges, emailValidations)
        .addFieldValidations(passwordContainer.id,
                passwordChanges,
                passwordValidations)
        .build()

submit.setOnClickListener {
    form.doSubmit()
}
```
Full sample [here](https://github.com/youse-seguradora/form/blob/master/app/src/main/kotlin/br/com/youse/forms/form/FormLoginActivity.kt)

How to RxForm
--------
RxForm [README](https://github.com/youse-seguradora/form/blob/master/rxform/README.MD)

How to LiveDataForm
--------
LiveDataForm [README](https://github.com/youse-seguradora/form/blob/master/livedata-form/README.MD)

VALIDATION CALLBACKS
--------
There are four standard callbacks in every flavor of Form, depending on the form flavor you choose to use the events are delivered as a pure Kotlin callbacks, an `Observable` emissions or `LiveData` events.
Does not matter the flavor, the behavior and naming should be consistent enough to understand what is happening.

### FieldValidationChange
 - Is triggered when a given field validation state change. This callback gives access to a `List<ValidationMessage>` that if it is empty, the field is valid.
 This callback allows you to set/unset a error message for each field in the form.

### FormValidationChange
 - Is triggered when the form validation state change. A boolean is used to indicate if the form is valid as a whole or not.
 This callback allow you to enable or disable a submit button or hint the user about the form state.

### ValidSubmit
 - Is triggered when a valid submit happens. This callback gives access to a `List<Pair<T, Any>>` where `T` is a field key, and the `Any` is that field current value.
 This callback is called to allow you to send the form data to your server.

### SubmitFailed
 - Is triggered when a submit happens but the form is not valid. This callback gives access to a `List<Pair<T, List<ValidationMesage>>>` where `T` is a field key and `List<ValidationMessage>` indicates how many validations failed for that given field.
 This callback is used when you need to scroll to a invalid field after a submit.


VALIDATION
--------
As an optional dependency we offer the possibility to use our validators module.

A form validation is related to each field validation,
so we need to define a list of validations for each field in the form.
You can define your own validators by implementing the `Validator<T>` interface,
in the sample below we define an `HoursValidator` and a `ValidationMessage`. It takes a `message` to be shown
to the user when the validation fails and a `ValidationType` that was created to
specify which type of validation failed (you can have multiple validators with the same `ValidationType`).

Note that your form can validate any type of input, here we used `String`,
but your implementation can be of any class that you define and implements the `isValid` method.

``` kotlin
class HoursValidator(val message: String, val divider: String) : Validator<String> {
    override fun validationMessage(): ValidationMessage {
        return ValidationMessage(message = message, validationType = HOUR_FORMAT)
    }

    override fun isValid(input: String): Boolean {
        return try {
            val parts = input.split(divider)
            val hours = parts.firstOrNull()?.toInt() ?: -1
            val minutes = parts.lastOrNull()?.toInt() ?: -1
            (parts.size == 2
                    && hours >= 0
                    && hours <= 23
                    && minutes >= 0
                    && minutes <= 59)
        } catch (e: Throwable) {
            false
        }
    }
}
```

FORMATTERS
--------
As an optional dependency we offer the possibility to use our formatters module.
Usually a formatter is coupled with the field validation. A field that displays hour and minutes (HH:MM) could be valid
if the the hours and minutes are valid only if in a given format.
So a given formatter is used together with a given validator.

``` kotlin
// 00:00
class HoursFormatter(val divider: String) : TextFormatter {
    private val digitsOnlyRegex = "[^0-9]".toRegex()

    override fun getCursorPosition(previous: CharSequence, input: CharSequence, output: CharSequence) = output.length

    override fun format(input: CharSequence): CharSequence {
        val clearText = input.toDigitsOnly()

        return when (clearText.length) {
            0, 1 -> clearText
            2, 3, 4 -> clearText.substring(0, 2) + divider + clearText.substring(2)
            else -> clearText.substring(0, 2) + divider + clearText.substring(2, 4)
        }
    }

    private fun CharSequence.toDigitsOnly(): String {
        return replace(digitsOnlyRegex, "")
    }
}
```
Note that each implementation of `TextFormatter` has a `getCursorPosition()` method that should return
where the field cursor should be positioned after each change in the input field. A more general interface `Formatter<T>` can be used
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
implementation 'br.com.youse.form:rxform:0.0.1'
implementation 'br.com.youse.form:validators:0.0.1' // optional
implementation 'br.com.youse.form:formatters:0.0.1' // optional
```


