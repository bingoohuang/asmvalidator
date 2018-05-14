[![Build Status](https://travis-ci.org/bingoohuang/asmvalidator.svg?branch=master)](https://travis-ci.org/bingoohuang/asmvalidator)
[![Coverage Status](https://coveralls.io/repos/github/bingoohuang/asmvalidator/badge.svg?branch=master)](https://coveralls.io/github/bingoohuang/asmvalidator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.bingoohuang/asmvalidator/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.bingoohuang/asmvalidator/)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# asmvalidator
java bean validator based on asm.

Examples for JAVABEAN.
```java
public class Person {
    String name;
    String addr;
    
    @AsmPast Date date;
    @AsmPast(format = "yyyy-MM-dd")
    String date2;
    
    @AsmFuture Date date;
    @AsmFuture(format = "yyyy-MM-dd")
    String date;
         
    @AsmIgnore String code;
    
    @AsmMaxSize(20) @AsmMessage("节目名称不能为空，长度不能超过20")
    String playName;
    
    @AsmMaxSize(7864320) @AsmMessage("图片不能超过5M")
    String portrait;

    @AsmSize(2) List<String> addresses;

    @AsmBlankable @AsmSize(6) String province;
    
    @AsmEmail String email;
    @AsmMobile String mobile;
    @AsmMobileOrEmail String mobileOrEmail;

    @AsmNotBlank AtomicBoolean some;
    
    @AsmMinSize(3) @AsmMaxSize(10) @AsmSize(4) int age;
    @AsmBlankable @AsmRegex("^\\w+$") String addr;
    
    @AsmRange("[10,100]") int age;
    @AsmRange("[A00,B99)") String addr;
    @AsmRange("男,女") String sex;
    @AsmRange("1,5,10,20,50,100") int rmb;
    @AsmRange("[10,]") int ageMin;
    @AsmRange("[,10]") int ageMax;
    @AsmRange("[A00,]") String code;
    @AsmRange("[A0,") String upperBound;
    
    @AsmUUID String uuid;
    
    @AsmBase64 String base64;
    @AsmBase64(purified = true) String other;
    @AsmBase64(format = UrlSafe) String third;
        
    @UrlsChecker
    List<String> urls;
    
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
    
    @AsmBlankable @MsaSex private String sex;
    @AsmNotBlank @MsaSex(allowLadyboy = true) private String sex2;
    
    
    @AsmConstraint(validateBy = MsaSex.MsaSexValidator.class)
    @Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MsaSex {
        boolean allowLadyboy() default false;
    
        class MsaSexValidator implements MsaValidator<MsaSex, String> {
            @Override
            public void validate(MsaSex msaSex, AsmValidateResult result, String sex) {
                if ("男".equals(sex) || "女".equals(sex)) return;
                if (msaSex.allowLadyboy() && "人妖".equals(sex)) return;
    
                result.addError(new ValidateError("sex", sex, "性别非法"));
            }
    
        }
    }
    
    public static void main(String[] args) {
        Person person = new Person();
        // set person properties...
        
        // validate person bean
        AsmValidatorFactory.validateWithThrow(bean);
    }
}


```