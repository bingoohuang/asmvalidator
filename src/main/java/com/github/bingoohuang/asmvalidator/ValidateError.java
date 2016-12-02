package com.github.bingoohuang.asmvalidator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString @AllArgsConstructor
public class ValidateError {
    @Getter String fieldName;
    @Getter final Object fieldValue;
    @Getter final String errorMessage;

    public ValidateError replaceFieldName(String oldName, String newName) {
        if (StringUtils.equals(oldName, fieldName)) fieldName = newName;
        return this;
    }
}
