package com.github.bingoohuang.asmvalidator.validation;

public class AsmFalseValidatorGenerator extends AsmTrueValidatorGenerator {
    @Override
    protected String useRegex() {
        return "(?i)false|no|off";
    }
}
