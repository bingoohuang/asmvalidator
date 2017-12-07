package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidateResult;
import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.MsaValidator;
import com.github.bingoohuang.asmvalidator.ValidateError;
import com.github.bingoohuang.asmvalidator.annotations.AsmConstraint;
import com.github.bingoohuang.asmvalidator.annotations.AsmMaxSize;
import com.github.bingoohuang.asmvalidator.annotations.AsmMessage;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class AfTvPlayBeanTest {
    @Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
    @AsmConstraint(supportedClasses = List.class, validateBy = UrlsChecker.AsmUrlsValidator.class)
    public @interface UrlsChecker {
        class AsmUrlsValidator implements MsaValidator<UrlsChecker, List<String>> {
            @Override public void validate(UrlsChecker annotation, AsmValidateResult result, List<String> urls) {
                for (int i = 0, ii = urls.size(); i < ii; ++i) {
                    val url = urls.get(i);
                    if (StringUtils.isEmpty(url)) {
                        result.addError(new ValidateError("urls_" + i, url, "URL不能为空"));
                    } else if (url.length() > 2) {
                        result.addError(new ValidateError("urls_" + i, url, "URL长度不能超过2"));
                    }
                }

            }
        }
    }

    @Data
    public static class AfTvPlayBean {
        @AsmMaxSize(2) @AsmMessage("节目名称不能为空，长度不能超过20")
        private String playName;
        @UrlsChecker
        private List<String> urls;
    }

    @Test
    public void test() {
        val afTvPlayBean = new AfTvPlayBean();

        afTvPlayBean.setPlayName("长长的");
        afTvPlayBean.setUrls(Lists.newArrayList("aaa", "bbb"));

        val result = new AsmValidateResult();
        AsmValidatorFactory.validate(afTvPlayBean, result);
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors().get(0)).isEqualTo(new ValidateError("playName", "长长的", "节目名称不能为空，长度不能超过20"));
        assertThat(result.getErrors().get(1)).isEqualTo(new ValidateError("urls_0", "aaa", "URL长度不能超过2"));
        assertThat(result.getErrors().get(2)).isEqualTo(new ValidateError("urls_1", "bbb", "URL长度不能超过2"));
    }
}
