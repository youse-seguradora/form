/**
MIT License

Copyright (c) 2018 Youse Seguros

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package br.com.youse.forms.validators

import kotlin.jvm.JvmStatic

data class ValidationStrategy(val beforeSubmit: Boolean,
                              val onSubmit: Boolean,
                              val afterSubmit: Boolean,
                              val onChange: Boolean,
                              val onEnable: Boolean,
                              val onTrigger: Boolean,
                              val clearErrorsOnDisable: Boolean,
                              val clearErrorOnChange: Boolean) {

    companion object {
        /**
         * Flag to start validating as soo as the form is created.
         */

        @JvmStatic
        val ALL_TIME = ValidationStrategy(
                beforeSubmit = true,
                onSubmit = true,
                afterSubmit = true,
                onChange = true,
                onEnable = true,
                onTrigger = true,
                clearErrorsOnDisable = true,
                clearErrorOnChange = false
        )
        /**
         * Flag to start validating the form only after the first submit event.
         */

        @JvmStatic
        val AFTER_SUBMIT = ValidationStrategy(
                beforeSubmit = false,
                onSubmit = true,
                afterSubmit = true,
                onChange = true,
                onEnable = true,
                onTrigger = true,
                clearErrorsOnDisable = true,
                clearErrorOnChange = false
        )
        /**
         * Flag to only validate the form when a submit or trigger events happens.
         */

        @JvmStatic
        val ON_SUBMIT = ValidationStrategy(
                beforeSubmit = false,
                onSubmit = true,
                afterSubmit = false,
                onChange = false,
                onEnable = false,
                onTrigger = true,
                clearErrorsOnDisable = true,
                clearErrorOnChange = true)
    }
}
