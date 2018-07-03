package br.com.youse.forms.validators

class ValidationTypes {
    companion object {
        val REQUIRED = object : ValidationType {}

        val MIN_LENGTH = object : ValidationType {}

        val HOUR_FORMAT = object : ValidationType {}
    }
}
