package com.example.sendotpfirebase
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter


class AdapterBinding {
    companion object {
        @BindingAdapter("app:error")
        @JvmStatic
        fun setError(edit: EditText, error: String?) {

                edit.error = error

        }

        @BindingAdapter("app:showInputForEditText")
        @JvmStatic
        fun showInputForEditText(edit: EditText, error: Boolean) {
            if (error) {
                edit.requestFocus( )
            }
        }
    }
}