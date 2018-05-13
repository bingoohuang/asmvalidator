package com.github.bingoohuang.asmvalidator.validator.domain;


import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class Person {
    String name;
    String addr;

    @AsmIgnore String code;

    public Person(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }
}
