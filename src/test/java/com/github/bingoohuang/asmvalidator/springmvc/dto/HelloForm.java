package com.github.bingoohuang.asmvalidator.springmvc.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class HelloForm {
    @Size(min = 1) String name;
}
