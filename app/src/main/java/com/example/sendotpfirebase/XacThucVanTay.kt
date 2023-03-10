package com.example.sendotpfirebase

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.sendotpfirebase.databinding.ActivityXacThucVanTayBinding
import java.security.KeyStore
import java.util.Arrays
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class XacThucVanTay : AppCompatActivity() {
    private lateinit var binding: ActivityXacThucVanTayBinding
    private lateinit var authu: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @RequiresApi(Build.VERSION_CODES.M)
    private var cipher = getCipher()
private   var x:ByteArray ? =null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityXacThucVanTayBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        Hàm getSecretKey() được sử dụng để tạo và trả về một đối tượng SecretKey được tạo trong AndroidKeyStore,
//        và được yêu cầu sử dụng xác thực người dùng, thường là xác thực vân tay, để truy cập vào nó.
//
//        Hàm này được sử dụng trong việc mã hóa và giải mã dữ liệu bảo mật trong ứng dụng của bạn bằng cách
//        sử dụng Cipher và khóa bí mật (SecretKey) được tạo ra trong AndroidKeyStore.
//
//        Chúng ta có thể sử dụng khóa bí mật này để mã hóa và giải mã dữ liệu trong ứng dụng của mình,
//        đồng thời yêu cầu người dùng xác thực bằng cách sử dụng vân tay để truy cập vào nó.
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Đăng nhập sinh trắc học cho ứng dụng của tôi")
            .setSubtitle("Đăng nhập bằng thông tin sinh trắc học của bạn")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)// cho biết cách hỗ trợ xác thực bằng thông tin xác thực sinh trắc học Lớp 3 hoặc khóa màn hình.
            .build()
        authu = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object :
            BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            @SuppressLint("LongLogTag")
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                if(x==null){
                    x=result.cryptoObject?.cipher?.getIV()
                    Log.d("onAuthenticationSucceeded", "onAuthenticationSucceeded: 1"   )
Toast.makeText(this@XacThucVanTay,"ok1",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this@XacThucVanTay,"ok1"+Arrays.equals(x,result.cryptoObject?.cipher?.getIV()),Toast.LENGTH_LONG).show()

                    Log.d("onAuthenticationSucceeded", "onAuthenticationSucceeded: "+ Arrays.equals(x,result.cryptoObject?.cipher?.getIV()))
                }


            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }
        })
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Log.d("MY_APP_TAG", "Ứng dụng có thể xác thực bằng sinh trắc học.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e(
                    "MY_APP_TAG",
                    "Không có tính năng sinh trắc học nào khả dụng trên thiết bị này.."
                )
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("MY_APP_TAG", "Các tính năng sinh trắc học hiện không khả dụng.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Nhắc người dùng tạo thông tin xác thực mà ứng dụng của bạn chấp nhận.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                enrollIntent.putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
                startActivityForResult(enrollIntent, 1)
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                TODO()
            }
        }
        binding.xacThuc.setOnClickListener {
//            val secretKey = getSecretKey()
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey)//CHẾ ĐỘ MÃ HÓA
//            val cancellationSignal = CancellationSignal()
            authu.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    //Các phần sau đây đi qua các ví dụ về việc sử dụng một Cipher đối tượng và một SecretKeyđối tượng để mã hóa dữ liệu
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getSecretKey(): SecretKey {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            "MySecretKeyName",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .build()

        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getCipher(): Cipher {
        val cipher =
            Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        return cipher
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }
}