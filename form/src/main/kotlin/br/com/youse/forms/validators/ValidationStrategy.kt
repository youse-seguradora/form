package br.com.youse.forms.validators

enum class ValidationStrategy {
    /**
     * Flag to start validating as soo as the form is created.
     */
    ALL_TIME,
    /**
     * Flag to start validating the form only after the first submit event.
     */
    AFTER_SUBMIT
}