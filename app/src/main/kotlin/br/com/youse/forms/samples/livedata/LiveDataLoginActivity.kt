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
package br.com.youse.forms.samples.livedata

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import br.com.youse.forms.R
import br.com.youse.forms.databinding.LiveDataActivityBinding
import br.com.youse.forms.samples.home.HomeActivity
import br.com.youse.forms.validators.ValidationStrategy

class LiveDataLoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val param = intent.getStringExtra("ValidationStrategy") ?: ValidationStrategy.AFTER_SUBMIT.name
        val strategy = ValidationStrategy.valueOf(param)

        val binding: LiveDataActivityBinding = DataBindingUtil.setContentView(this, R.layout.live_data_activity)

        val vm: LoginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        vm.createForm(strategy)
        binding.vm = vm
        binding.setLifecycleOwner(this)

        vm.onSubmit.observe(this, object : LiveEventObserver<LoginState>() {
            override fun onEventChanged(event: LoginState?) {
                event?.data?.let {
                    handleSuccess()
                }
            }
        })

    }

    fun handleSuccess() {
        Toast.makeText(this, "Data sent to server \\o/", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, HomeActivity::class.java))
    }


}
