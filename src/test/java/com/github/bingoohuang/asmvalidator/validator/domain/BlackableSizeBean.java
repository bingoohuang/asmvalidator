package com.github.bingoohuang.asmvalidator.validator.domain;

import com.github.bingoohuang.asmvalidator.annotations.*;
import lombok.Data;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2017/2/10.
 */
@Data @AsmCreateClassFile4Debug
public class BlackableSizeBean {
    @AsmMaxSize(7864320) @AsmMessage("图片不能超过5M")
    private String portrait;

    @AsmBlankable @AsmSize(6)
    private String province;
}
