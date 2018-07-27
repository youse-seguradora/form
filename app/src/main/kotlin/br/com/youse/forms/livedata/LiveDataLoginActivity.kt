package br.com.youse.forms.livedata

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.youse.forms.R
import br.com.youse.forms.databinding.LiveDataActivityBinding
import br.com.youse.forms.validators.ValidationMessage
import kotlinx.android.synthetic.main.live_data_activity.*

class LiveDataLoginActivity : AppCompatActivity() {
    lateinit var vm: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: LiveDataActivityBinding = DataBindingUtil.setContentView(this, R.layout.live_data_activity)

        vm = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding.vm = vm
        binding.bla = this

        vm.form.onFieldValidationChange.observe(this, object : Observer<Pair<String, List<ValidationMessage>>> {
            override fun onChanged(t: Pair<String, List<ValidationMessage>>?) {
                if (t?.first == "Email") {
                    emailContainer.error = t.second.joinToString { it.message }
                }
                if (t?.first == "Password") {
                    passwordContainer.error = t.second.joinToString { it.message }
                }
            }
        })

        vm.success.observe(this, Observer {
            println(it?.toString())
        })

    }


}
