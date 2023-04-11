package com.awbd.bankingApp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IbanGenerator {
    private static final Map<String, String> bankCodes = new HashMap<>();

    static {
        bankCodes.put("BCR", "RNCB");
        bankCodes.put("BRD", "BRDE");
        bankCodes.put("ING", "INGB");
        bankCodes.put("Raiffeisen", "RZBR");
        bankCodes.put("Banca Transilvania", "BTRL");
        bankCodes.put("Cec Bank", "CECE");
        bankCodes.put("Alpha Bank", "BUCU");
        bankCodes.put("UniCredit Bank", "BACX");
        bankCodes.put("ProCredit Bank", "PRCU");
        bankCodes.put("Garanti Bank", "TGBV");
    }

    public static String generateIbanRo() {
        Random random = new Random();
        String[] bankNames = bankCodes.keySet().toArray(new String[0]);
        String bankName = bankNames[random.nextInt(bankNames.length)];
        String bankCode = bankCodes.get(bankName);
        String countryCode = "RO";
        String accountNumber = "";
        for (int i = 0; i < 16; i++) {
            accountNumber += random.nextInt(10);
        }
        String iban = countryCode + bankCode + accountNumber + "RO";
        return iban;
    }
}
