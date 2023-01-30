package org.standardserve.driftflasche.login;

import android.text.TextUtils;
import android.util.Log;

import java.util.Objects;

// Class to validate text input
public class textValidation {

    private textValidation(){}

    // Method to validate email
    public static boolean emailValidation(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to validate password
    public static boolean passwordValidation(String password){
        return password.length() >= 6;
    }

    // Method to validate username
    public static boolean repeatPasswordValidation(String password, String repeatPassword){
        return Objects.equals(password, repeatPassword);
    }

    // Method to validate username
    public static boolean truenameValidation(String truename){
        return truename.length() >= 2 && truename.length() <= 20;
    }

    // Method to validate comment length
    public static boolean limitCommentLength(String comment){
        return comment.length() <= 20;
    }

    // Method to validate bottle text length
    public static boolean limitBottleTextLength(String bottleText){
        return bottleText.length() <= 100;
    }
}