LiveDataForm
========

LiveData version of `Form`

How to `LiveDataForm`
--------
1) Create a `LiveDataForm.Builder<T>(submitObservable)` where `T` is the type of each field key;
2) Call `addFieldValidations()` as many fields in the screen using as parameters a key, a `LiveData` and a list of validators;
3) Execute the `build()` method to get a hold of a `RxForm` instance;
4) Observe to the LiveData available from the `LiveDataForm` instance methods;


``` kotlin
// In the ViewModel

val EMAIL = "email"
val PASSWORD = "password"

val email = MutableLiveData<CharSequence>()

val password = MutableLiveData<CharSequence>()

private val emailValidations ...
private val passwordValidations ...

val form = LiveDataForm.Builder<String>()
        .addFieldValidations(EMAIL,
                email, emailValidations)
        .addFieldValidations(PASSWORD,
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
    app:onFieldValidationChange="@{vm.form.onFieldValidationChange[vm.EMAIL]}">

    <android.support.design.widget.TextInputEditText
        android:id="@+id/email"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textEmailAddress"
        app:formField="@={vm.email}" />
</android.support.design.widget.TextInputLayout>
...
<Button
    android:id="@+id/submit"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Submit"
    app:formSubmit="@={vm.form.submit}" />
```

Full sample [here](https://github.com/youse-seguradora/form/blob/master/app/src/main/kotlin/br/com/youse/forms/livedata/LiveDataLoginActivity.kt)

BindingAdapters and LiveData
--------

### form.onFieldValidationChange
 - Is a `Map<T, MutableLiveData>` where each LiveData will emit when the field of key T suffers a validation change.
 It is usually used together with the XML tag `app:onFieldValidationChange`.

### form.submit
 - The LiveDataForm class uses this MediatorLiveData as validation bootstrap. This should be bound to the form submission button
 using the tag `app:formSubmit`. NOTE: The Boolean being emitted has no real value, it exist just to overcome a DataBinding limitation.

### form.onValidSubmit
 - It emits when a valid submit happens. This LiveData gives access to a `List<Pair<T, Any?>>` where `T` is a field key, and the `Any` is that field current value.
 This LiveData is called to inform you when to send the form data to your server.

### form.onSubmitFailed
 - It emits when a submit happens but the form is not valid. This LiveData gives access to a `List<Pair<T, List<ValidationMesage>>>` where `T` is a field key and `List<ValidationMessage>` indicates how many validations failed for that given field.
 This LiveData is used when you need to scroll to a invalid field after a submit.

Download
--------

Download or grab via Maven:
```xml
<dependency>
  <groupId>br.com.youse.form</groupId>
  <artifactId>livedata-form</artifactId>
  <version>0.0.1</version>
</dependency>
```
or Gradle:
```groovy
implementation 'br.com.youse.form:livedata-form:0.0.1'
implementation 'br.com.youse.form:validators:0.0.1' // optional
implementation 'br.com.youse.form:formatters:0.0.1' // optional
```


