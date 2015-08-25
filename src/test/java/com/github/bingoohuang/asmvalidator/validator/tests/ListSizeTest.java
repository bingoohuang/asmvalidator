package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmSize;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ListSizeTest {
    public static class ListSizeBean {
        @AsmSize(2)
        List<String> addresses;

        public List<String> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<String> addresses) {
            this.addresses = addresses;
        }
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
        } catch (AsmValidateException ex) {
            assertTrue(ex.getMessage().contains("长度不等于2"));
        }

    }
}
