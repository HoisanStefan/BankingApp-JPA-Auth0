package com.awbd.bankingApp.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AccountTypeConverter implements AttributeConverter<AccountType, String> {
    @Override
    public String convertToDatabaseColumn(AccountType attribute) {
        return attribute.name();
    }

    @Override
    public AccountType convertToEntityAttribute(String dbData) {
        return AccountType.valueOf(dbData);
    }
}
