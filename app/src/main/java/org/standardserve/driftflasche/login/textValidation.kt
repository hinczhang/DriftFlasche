package org.standardserve.googletestunit.login

import android.text.TextUtils

object textValidation {
    fun emailValidation(email: String) = !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    fun passwordValidation(password: String) = password.length >= 6
    fun repeatPasswordValidation(password: String, repeatPassword: String) = password == repeatPassword
    fun truenameValidation(truename: String) = truename.length in 2..20
}