package com.awbd.bankingApp.utils;

import com.awbd.bankingApp.exceptions.InvalidPhoneNumberException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator {
    public static void validate(String phoneNumber) throws InvalidPhoneNumberException {
        String regex = "^(?:\\+40|0)?7[0-9]{8}$"; // Romanian phone number format
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()) {
            throw new InvalidPhoneNumberException("Invalid phone number for Romania");
        } else {
            System.out.println("Valid phone number for Romania");
        }
    }
}
