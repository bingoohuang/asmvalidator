package com.github.bingoohuang.asmvalidator.paramvalidator.tests;

import com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory;
import com.github.bingoohuang.asmvalidator.annotations.AsmFuture;
import com.github.bingoohuang.asmvalidator.annotations.AsmValid;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.bingoohuang.asmvalidator.AsmParamsValidatorFactory.createValidatorSignature;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class AsmFutureParamTest {
    private static String stringSignature;

    public interface FutureStringApi {
        @AsmValid
        String getAreaByDistrict(
                @AsmFuture(format = "yyyy-MM-dd")
                        String future);
    }

    @BeforeClass
    public static void beforeClass() {
        Method method = FutureStringApi.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        stringSignature = createValidatorSignature(method);

        method = FutureDateApi.class.getMethods()[0];
        AsmParamsValidatorFactory.createValidators(method);
        dateSignature = createValidatorSignature(method);
    }

    @Test
    public void testString() {
        AsmParamsValidatorFactory.validate(stringSignature, "2055-09-11");
    }

    @Test
    public void testStringBad() {
        try {
            AsmParamsValidatorFactory.validate(stringSignature, "2011-09-11");
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("需要一个将来的时间");
        }
    }

    private static String dateSignature;

    public interface FutureDateApi {
        @AsmValid
        String getAreaByDistrict(
                @AsmFuture
                        Date future);
    }


    @Test
    public void testDate() throws ParseException {
        AsmParamsValidatorFactory.validate(dateSignature,
                new SimpleDateFormat("yyyy-MM-dd").parse("2055-09-11"));
    }

    @Test
    public void testDateBad() {
        try {
            AsmParamsValidatorFactory.validate(dateSignature,
                    new SimpleDateFormat("yyyy-MM-dd").parse("2011-09-11"));
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("需要一个将来的时间");
        }
    }

}
