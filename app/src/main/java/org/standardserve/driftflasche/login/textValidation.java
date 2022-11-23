package org.standardserve.driftflasche.login;

import android.text.TextUtils;
import android.util.Log;

import java.util.Objects;

public class textValidation {
    public static boolean emailValidation(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean passwordValidation(String password){

        return password.length() >= 6;
    }
    public static boolean repeatPasswordValidation(String password, String repeatPassword){
        return Objects.equals(password, repeatPassword);
    }
    public static boolean truenameValidation(String truename){
        return truename.length() >= 2 && truename.length() <= 20;
    }
    //TODO: limit upload text information
    public static boolean limitCommentLength(String comment){
        return comment.length() <= 20;
    }

    public static boolean limitBottleTextLength(String bottleText){
        return bottleText.length() <= 100;
    }
}