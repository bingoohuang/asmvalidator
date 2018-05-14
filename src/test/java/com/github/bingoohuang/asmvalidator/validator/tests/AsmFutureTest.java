package com.github.bingoohuang.asmvalidator.validator.tests;

import com.github.bingoohuang.asmvalidator.AsmValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmFuture;
import lombok.Data;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class AsmFutureTest {
    @Data
    public static class AsmPastBean1 {
        @AsmFuture
        Date date;
    }

    @Test
    public void testDate() throws ParseException {
        AsmPastBean1 bean = new AsmPastBean1();
        bean.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2055-09-11"));
        AsmValidatorFactory.validateWithThrow(bean);
    }

    @Test
    public void testDateBad() throws ParseException {
        AsmPastBean1 bean = new AsmPastBean1();
        bean.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2001-09-11"));
        try {
            AsmValidatorFactory.validateWithThrow(bean);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("需要一个将来的时间");
        }
    }

    @Data
    public static class AsmPastBean2 {
        @AsmFuture
        Calendar date;
    }

    @Test
    public void testCalendar() {
        AsmPastBean2 bean = new AsmPastBean2();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis() + 100);
        bean.setDate(instance);
        AsmValidatorFactory.validateWithThrow(bean);
    }

    @Test
    public void testCalendarBad() {
        AsmPastBean2 bean = new AsmPastBean2();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis() - 100);
        bean.setDate(instance);

        try {
            AsmValidatorFactory.validateWithThrow(bean);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("需要一个将来的时间");
        }
    }

    @Data
    public static class AsmPastBean3 {
        @AsmFuture(format = "yyyy-MM-dd")
        String date;
    }

    @Test
    public void testString() {
        AsmPastBean3 bean = new AsmPastBean3();
        bean.setDate("2055-09-11");
        AsmValidatorFactory.validateWithThrow(bean);
    }

    @Test
    public void testStringBad() {
        AsmPastBean3 bean = new AsmPastBean3();
        bean.setDate("2001-09-11");
        try {
            AsmValidatorFactory.validateWithThrow(bean);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("需要一个将来的时间");
        }
    }
}
