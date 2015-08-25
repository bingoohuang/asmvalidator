package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.validator.domain.EmailBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ValidEmailTest {
    private final String email;

    @Parameterized.Parameters
    public static Collection<Object> validEmails() {
        return Arrays.asList(new Object[]{"mkyong@yahoo.com",
                "mkyong-100@yahoo.com", "mkyong.100@yahoo.com",
                "mkyong111@mkyong.com", "mkyong-100@mkyong.net",
                "mkyong.100@mkyong.com.au", "mkyong@1.com",
                "mkyong@gmail.com.com", "mkyong+100@gmail.com",
                "mkyong-100@yahoo-test.com"});
    }

    public ValidEmailTest(String email) {
        this.email = email;
    }

    @Test
    public void test1() {
        EmailBean emailBean = new EmailBean();
        emailBean.setEmail(email);
        AsmValidatorFactory.validateWithThrow(emailBean);
    }
}
