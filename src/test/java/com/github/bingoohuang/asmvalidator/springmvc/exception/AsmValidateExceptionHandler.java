package com.github.bingoohuang.asmvalidator.springmvc.exception;

import com.github.bingoohuang.asmvalidator.ex.AsmValidateException;
import com.github.bingoohuang.utils.net.Http;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/12/5.
 */
@ControllerAdvice
public class AsmValidateExceptionHandler {
    @ExceptionHandler(AsmValidateException.class)
    public void handleConflict(AsmValidateException ex, HttpServletResponse response) {
        Http.error(response, 500, ex);
    }
}
