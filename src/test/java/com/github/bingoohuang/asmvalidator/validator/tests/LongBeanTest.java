package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import org.junit.Test;

public class LongBeanTest {
    @Test
    public void test() {
        LongBean longBean = new LongBean();
        longBean.setMoney(100L);
        AsmValidatorFactory.validateWithThrow(longBean);
    }

    public static class LongBean {
        private long money;

        public long getMoney() {
            return money;
        }

        public void setMoney(long money) {
            this.money = money;
        }
    }
}
