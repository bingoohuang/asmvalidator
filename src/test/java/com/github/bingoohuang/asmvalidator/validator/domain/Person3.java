package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.*;
import lombok.Data;

@Data
public class Person3 {
    @AsmMinSize(3) @AsmMaxSize(10) @AsmSize(4) int age;
    @AsmBlankable @AsmRegex("^\\w+$") String addr;
}
