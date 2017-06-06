package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmSize;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.val;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ListSizeTest {
    @Data
    public static class ListSizeBean {
        @AsmSize(2)
        List<String> addresses;
    }

    @Data
    public static class ListBean {
        List<String> addresses;
    }

    @Test
    public void test1() {
        val listBean = new ListBean();
        listBean.setAddresses(Lists.newArrayList("xxx", "yyyy"));
        AsmValidatorFactory.validateWithThrow(listBean);
    }

    @Test
    public void listSizeOK() {
        ListSizeBean listSizeBean = new ListSizeBean();
        List<String> addresses = Lists.newArrayList("xxx", "yyyy");
        listSizeBean.setAddresses(addresses);

        AsmValidatorFactory.validateWithThrow(listSizeBean);
    }

    @Test
    public void listSize() {
        ListSizeBean listSizeBean = new ListSizeBean();
        List<String> addresses = Lists.newArrayList("xxx");
        listSizeBean.setAddresses(addresses);

        try {
            AsmValidatorFactory.validateWithThrow(listSizeBean);
            fail();
        } catch (AsmValidateException ex) {
            assertTrue(ex.getMessage().contains("长度不等于2"));
        }

    }
}
