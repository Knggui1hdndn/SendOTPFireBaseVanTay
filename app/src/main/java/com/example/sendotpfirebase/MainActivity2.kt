package com.example.sendotpfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.sendotpfirebase.databinding.ActivityMain2Binding
import com.example.sendotpfirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity2 : AppCompatActivity() {
    var intent1: Intent? = null
    private lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
        binding.viewModel = ViewModel()
        intent1 = intent
        binding.a.text = intent1?.getStringExtra("phoneAuth")
        binding.executePendingBindings()

    }

    class ViewModel {
        fun OnClickDangXuat(view: View) {
            FirebaseAuth.getInstance().signOut()
            (view.context as MainActivity2).finish()
            val intent = Intent(view.context, MainActivity::class.java)
            view.context.startActivity(intent)
        }
    }
}