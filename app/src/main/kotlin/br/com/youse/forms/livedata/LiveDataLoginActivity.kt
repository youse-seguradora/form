package br.com.youse.forms.livedata

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.youse.forms.R
import br.com.youse.forms.databinding.LiveDataActivityBinding
import kotlinx.android.synthetic.main.live_data_activity.*

class LiveDataLoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: LiveDataActivityBinding = DataBindingUtil.setContentView(this, R.layout.live_data_activity)

        val vm: LoginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding.vm = vm
        binding.setLifecycleOwner(this)

        vm.success.observe(this, Observer {
            println(it?.toString())
        })

    }


}
