package com.github.bingoohuang.asmvalidator.springmvctest;

import com.github.bingoohuang.asmvalidator.springmvc.Application;
import com.jayway.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(classes = Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")   // 4
public class CharacterControllerTest {
    @Value("${local.server.port}")
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void canHello2() {
        given().
                queryParam("name", "黄进兵").
                when().
                get("/hello2").
                then().
                statusCode(HttpStatus.SC_OK).
                body(equalTo("Hello 黄进兵"));
    }

    @Test
    public void canHello() throws Exception {
        given().
                queryParam("name", "黄进兵").
                when().
                get("/hello").
                then().
                statusCode(HttpStatus.SC_OK).
                body(equalTo("Hello 黄进兵"));
    }

    @Test
    public void canHelloNullName() throws Exception {
        given().
                when().
                get("/hello").
                then().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                body(containsString("字段不能为空"));
    }
}
