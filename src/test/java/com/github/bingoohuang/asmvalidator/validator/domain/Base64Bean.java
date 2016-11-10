package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmBase64;
import lombok.Data;

import static com.github.bingoohuang.asmvalidator.annotations.AsmBase64.Base64Format.UrlSafe;

@Data
public class Base64Bean {
    @AsmBase64 String base64;
    @AsmBase64(purified = true) String other;
    @AsmBase64(format = UrlSafe) String third;
}
