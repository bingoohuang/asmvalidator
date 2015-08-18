package com.github.bingoohuang.asmvalidator.custom;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import org.junit.Test;

public class MsaSexBeanTest {
    @Test
    public void validMale() {
        SexBean sexBean = new SexBean();
        sexBean.setSex("男");
        sexBean.setSex2("女");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    @Test
    public void validFemale() {
        SexBean sexBean = new SexBean();
        sexBean.setSex("女");
        sexBean.setSex2("女");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    @Test
    public void validLadyBody() {
        SexBean sexBean = new SexBean();
        sexBean.setSex("男");
        sexBean.setSex2("人妖");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    @Test(expected = AsmValidateException.class)
    public void validBadLadyBaoy() {
        SexBean sexBean = new SexBean();
        sexBean.setSex("人妖");
        sexBean.setSex2("人妖");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    @Test(expected = AsmValidateException.class)
    public void bad() {
        SexBean sexBean = new SexBean();
        sexBean.setSex("X");
        sexBean.setSex2("Y");
        AsmValidatorFactory.validateWithThrow(sexBean);
    }

    public static class SexBean {
        @MsaSex
        private String sex;
        @MsaSex(allowLadyboy = true)
        private String sex2;

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getSex2() {
            return sex2;
        }

        public void setSex2(String sex2) {
            this.sex2 = sex2;
        }
    }
}
