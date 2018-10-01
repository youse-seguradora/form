LiveDataForm
========

LiveData version of `Form`

How to `LiveDataForm`
--------
1) Create a `LiveDataForm.Builder<T>()` where `T` is the type of each field key;
2) Call `builder.addField()` as many `LiveField`s in the form;
3) Execute the `builder.build()` method to get a hold of a `LiveDataForm` instance;
4) Observe the `form.onFormValidationChange` MediatorLiveData<Boolean>, this will bootstrap the form validation. Without it the form will not work!!
5) Observe to the other LiveData available from the `LiveDataForm` instance methods;
6) Bind your `LiveField.input` and `LiveField.errors` to your XML layout or observe them in your Activity/Fragment;

``` kotlin
// In the ViewModel

private val EMAIL_KEY = "email"
private val PASSWORD_KEY = "password"

private val emailValidations ...
private val passwordValidations ...

val email = LiveField(key = EMAIL_ID, validators = emailValidators)
val password = LiveField(key = PASSWORD_ID, validators = passwordValidators)

val form: LiveDataForm<Int> = LiveDataForm.Builder<Int>()
        .addField(email)
        .addField(password)
        .build()
```

Sample below uses databinding-livedata-form module to observe the LiveData events.


```xml
...
<android.support.design.widget.TextInputLayout
    android:id="@+id/emailContainer"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldError="@{vm.email.errors}">

    <android.support.design.widget.TextInputEditText
        android:id="@+id/email"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textEmailAddress"
        app:field="@={vm.email.input}" />
</android.support.design.widget.TextInputLayout>
...
<Button
    android:id="@+id/submit"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Submit"
    app:formEnabled="@{vm.form.onFormValidationChange}"
    android:onClick="@={ _ -> vm.form.doSubmit()}" />
```

Full sample [here](https://github.com/youse-seguradora/form/blob/master/app/src/main/kotlin/br/com/youse/forms/samples/livedata/LiveDataLoginActivity.kt)

LiveField<T,R> properties
--------
## LiveField.key`<T>`
 - It's an ID to identify uniquely the field in a form. Of type `T`.

## LiveField.input`<R>`
 - It's an `MutableLiveData<R>` used to feed the field value change events to the form.
 - The developer should call `LiveField.input.value = newValue` whenever the field changes.
 - Usually bound to DataBinding in layout.

## LiveField.errors
 - It's a `List<ValidationMessage>` if the list is empty, then the field will be valid.
 - It's usually used to show or remove a validation error.
 - Usually bound to DataBinding in layout.

## LiveField.validators`<R>`
 - It's a list of `Validator<R>`.

## LiveField.validationTriggers
 - It's a list of `MutableLiveData<Unit>` used to force a round of validation to happen to this field.

LiveDataForm events
--------
## LiveDataForm.onFormValidationChange
 - The `LiveDataForm` class uses this `MediatorLiveData as validation bootstrap.
 - It's triggered when the form validation state change. A boolean is used to indicate if the form is valid as a whole or not.
 - This `LiveData` allow you to enable or disable a submit button.

## LiveDataForm.onValidSubmit
 - It emits when a valid submit happens. This `LiveData` gives access to a `List<Pair<T, Any?>>` where `T` is a field key, and the `Any?` is that field current value.
 - This `LiveData` is called to inform you when to send the form data to your server.

## LiveDataForm.onSubmitFailed
 - It emits when a submit happens but the form is not valid. This LiveData gives access to a `List<Pair<T, List<ValidationMessage>>>` where `T` is a field key and `List<ValidationMessage>` indicates how many validations failed for that given field.
 - This `LiveData` is used when you need to scroll to a invalid field after a submit.

Download
--------

Download or grab via Maven:
```xml
<dependency>
  <groupId>br.com.youse.forms</groupId>
  <artifactId>livedata-form</artifactId>
  <version>0.0.1</version>
</dependency>
```
add
```groovy
        maven { url 'https://oss.sonatype.org/content/groups/public' }
```
and
or Gradle:
```groovy
implementation 'br.com.youse.forms:livedata-form:0.0.1'
```
