package com.awbd.bankingApp.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CreditStateConverter implements AttributeConverter<CreditState, String> {
    @Override
    public String convertToDatabaseColumn(CreditState creditState) {
        return creditState.name();
    }

    @Override
    public CreditState convertToEntityAttribute(String dbData) {
        return CreditState.valueOf(dbData);
    }
}
