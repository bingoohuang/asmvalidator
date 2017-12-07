package com.github.bingoohuang.asmvalidator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Data @AllArgsConstructor
public class ValidateError {
    @Getter String fieldName;
    @Getter final Object fieldValue;
    @Getter final String errorMessage;

    public ValidateError replaceFieldName(String oldName, String newName) {
        if (StringUtils.equals(oldName, fieldName)) fieldName = newName;
        return this;
    }
}
