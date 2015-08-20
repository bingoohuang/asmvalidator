package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.github.bingoohuang.asmvalidator.validator.domain.EmailBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class InValidEmailTest {
    private final String email;

    @Parameterized.Parameters
    public static Collection<Object> invalidEmails() {
        return Arrays.asList(new Object[]{"mkyong", "mkyong@.com.my",
                "mkyong123@gmail.a", "mkyong123@.com", "mkyong123@.com.com",
                ".mkyong@mkyong.com", "mkyong()*@gmail.com", "mkyong@%*.com",
                "mkyong..2002@gmail.com", "mkyong.@gmail.com",
                "mkyong@mkyong@gmail.com", "mkyong@gmail.com.1a"});
    }

    public InValidEmailTest(String email) {
        this.email = email;
    }

    @Test (expected = AsmValidateException.class)
    public void test1() {
        EmailBean emailBean = new EmailBean();
        emailBean.setEmail(email);
        AsmValidatorFactory.validateWithThrow(emailBean);
    }
}
