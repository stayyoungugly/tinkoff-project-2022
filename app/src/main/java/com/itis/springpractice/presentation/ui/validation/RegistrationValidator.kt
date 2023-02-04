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

    fun isValidName(name: String): Boolean {
        val namePattern = "^[\\D]{2,15}\$"
        return !TextUtils.isEmpty(name) && compile(namePattern).matcher(name).matches()
    }

    fun isValidNickname(nickname: String): Boolean {
        val nicknamePattern = "^.{2,15}\$"
        return !TextUtils.isEmpty(nickname) && compile(nicknamePattern).matcher(nickname).matches()
    }
}
