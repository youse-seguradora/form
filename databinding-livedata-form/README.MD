DataBinding - LiveDataForm
========

DataBinding - LiveData version of `Form`

This module uses LiveDataForm to offer some binding adapters which may easy the usage of LiveDataForm.

``` kotlin
// In the ViewModel

val EMAIL_KEY = "email"
val PASSWORD_KEY = "password"

val email = MutableLiveData<CharSequence>()

val password = MutableLiveData<CharSequence>()

private val emailValidations ...
private val passwordValidations ...

val form = LiveDataForm.Builder<String>()
        .addFieldValidations(EMAIL_KEY,
                email, emailValidations)
        .addFieldValidations(PASSWORD_KEY,
                password,
                passwordValidations)
        .build()
```
```xml
...
<android.support.design.widget.TextInputLayout
    android:id="@+id/emailContainer"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldError="@{vm.onEmailValidationChange}">

    <android.support.design.widget.TextInputEditText
        android:id="@+id/email"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textEmailAddress"
        app:field="@={vm.email}" />
</android.support.design.widget.TextInputLayout>
...
<Button
    android:id="@+id/submit"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Submit"
    app:formEnabled="@{vm.form.onFormValidationChange}"
    app:submit="@={vm.form.submit}" />
```

Full sample [here](https://github.com/youse-seguradora/form/blob/master/app/src/main/kotlin/br/com/youse/forms/samples/livedata/LiveDataLoginActivity.kt)

BindingAdapters and LiveDataForm events
--------
### form.submit
 - The LiveDataForm class uses this MediatorLiveData as validation bootstrap. This should be bound to the form submission button
 using the tag `app:submit`. *NOTE*: The Boolean being emitted has no real meaning, it exist just to overcome a DataBinding limitation.

### form.onFieldValidationChange
 - Is a `Map<T, MutableLiveData>` where each LiveData will emit when the field of key T suffers a validation change.
 It is usually used together with the XML tag `app:fieldError` to show or remove a validation error.

### form.onFormValidationChange
 - Is triggered when the form validation state change. A boolean is used to indicate if the form is valid as a whole or not.
 This LiveData allow you to enable or disable a submit button using the XML tag `formEnabled`.

### form.onValidSubmit
 - It emits when a valid submit happens. This LiveData gives access to a `List<Pair<T, Any?>>` where `T` is a field key, and the `Any?` is that field current value.
 This LiveData is called to inform you when to send the form data to your server.

### form.onSubmitFailed
 - It emits when a submit happens but the form is not valid. This LiveData gives access to a `List<Pair<T, List<ValidationMessage>>>` where `T` is a field key and `List<ValidationMessage>` indicates how many validations failed for that given field.
 This LiveData is used when you need to scroll to a invalid field after a submit.

Download
--------

Download or grab via Maven:
```xml
<dependency>
  <groupId>br.com.youse.forms</groupId>
  <artifactId>databinding-livedata-form</artifactId>
  <version>0.0.1</version>
</dependency>
```
or Gradle:
add
```groovy
        maven { url 'https://oss.sonatype.org/content/groups/public' }
```
and
```groovy
implementation 'br.com.youse.forms:databinding-livedata-form:0.0.1'
```


