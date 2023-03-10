package com.example.sendotpfirebase

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import com.google.android.gms.dynamic.IFragmentWrapper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class VerifyViewModel : BaseObservable() {
    var soDT = ObservableField<String>()
    var otp = ObservableField<String>()

    @Bindable
    var checkSDT = false


    @Bindable
    var checkOTP = false

    @Bindable
    var checkLogin = false

    val auth = FirebaseAuth.getInstance()

    var verificationId1: String? = null

    var CODE: String  = ""

    fun Login(view: View) {
//        val inputMethodManager: InputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        val vie: IBinder? =(view.context as MainActivity).currentFocus?.windowToken;
//        inputMethodManager.hideSoftInputFromWindow(vie, 0)

        if (checkLogin && otp.get().toString() == CODE && CODE.isEmpty() && !checkOTP) {
             val credential = PhoneAuthProvider.getCredential(verificationId1!!, CODE)
            signInWithPhoneAuthCredential(credential, view)
        }else{

              checkSDT = soDT.get().isNullOrEmpty()
              checkOTP = otp.get().isNullOrEmpty()
            if (!checkSDT&&!checkLogin) {
                sendOTP(view)
                checkLogin = true
                notifyPropertyChanged(BR.checkLogin)
            }
              Toast.makeText(view.context,"$checkSDT    $checkOTP", Toast.LENGTH_SHORT).show()
              notifyPropertyChanged(BR.checkOTP)
              notifyPropertyChanged(BR.checkSDT)

         }


    }

    fun sendOTP(view: View) {
        auth.setLanguageCode("vn")
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+84" + soDT.get().toString())// Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(view.context as MainActivity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d(
                        "onVerification",
                        "onVerificationCompleted: " + credential.signInMethod
                    )
                     signInWithPhoneAuthCredential(credential, view)
                    CODE = credential.smsCode.toString()

                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.d("onVerification", "onVerificationCompleted: $e")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId1 = verificationId
                }

            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    @SuppressLint("SuspiciousIndentation")
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, view: View) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
             val intent = Intent(view.context,MainActivity2::class.java)
                intent.putExtra("phoneAuth",task.result.user?.displayName)
                view.context.startActivity(intent)
                (view.context as MainActivity).finish()

            } else {
                Toast.makeText(view.context, "!OK", Toast.LENGTH_SHORT).show()

            }
        }

    }
}