package com.madartech.sesim.utils;

public class Validation {

    public static boolean validateEmail(String email){
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
    public static boolean validatePassword(String password){
        return password.length() >= 8;
    }
    public static boolean passwordsMatch(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }
}
