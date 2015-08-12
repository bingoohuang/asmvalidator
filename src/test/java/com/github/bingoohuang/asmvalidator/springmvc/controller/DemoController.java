package com.github.bingoohuang.asmvalidator.springmvc.controller;

import com.github.bingoohuang.asmvalidator.springmvc.domain.Address;
import com.github.bingoohuang.asmvalidator.springmvc.dto.HelloForm;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class DemoController {
    @RequestMapping(value = "/hello",
            method = GET,
            produces = "application/json")
    public String hello(@RequestParam("name") String name,
                        BindingResult result) {
        if (result.hasErrors()) {
            return "error:";
        }
        return "Hello " + name;
    }

    @RequestMapping(value = "/hello2",
            method = GET,
            produces = "application/json")
    public String hello2(@Valid @ModelAttribute HelloForm helloForm,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "error:";
        }
        return "Hello " + helloForm.getName();
    }

    @RequestMapping(value = "/address",
            method = GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Address getAddress() {
        Address address = new Address();
        address.setState("FL");
        address.setStreet("12345 Horton Ave");

        return address;
    }
}
