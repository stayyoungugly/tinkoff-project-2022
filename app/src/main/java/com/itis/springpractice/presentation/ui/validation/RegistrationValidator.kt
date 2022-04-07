package com.itis.springpractice.presentation.ui.validation

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Pattern.compile

class RegistrationValidator {
    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}\$"
        return !TextUtils.isEmpty(password) && compile(passwordPattern).matcher(password).matches()
    }
}
