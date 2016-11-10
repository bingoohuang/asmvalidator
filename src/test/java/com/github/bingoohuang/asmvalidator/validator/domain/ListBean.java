package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import lombok.Data;

import java.util.List;

@Data
public class ListBean {
    @AsmIgnore private String name;
    private List<MobileBean> mobiles;
}
