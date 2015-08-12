package com.github.bingoohuang.asmvalidator.springmvctest;

import com.github.bingoohuang.asmvalidator.springmvc.controller.DemoController;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class DemoControllerTest {
    private final MockMvc mockMvc
            = standaloneSetup(new DemoController())
            .build();

    @Test
    public void address() throws Exception {
        mockMvc.perform(get("/address"))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.street").value("12345 Horton Ave"))
        ;
    }
}
