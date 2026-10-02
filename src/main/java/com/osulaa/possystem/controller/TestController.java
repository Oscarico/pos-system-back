package com.osulaa.possystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // GET http://localhost:8080/test
    @GetMapping("/test")
    public String test() {
        return "JWT funcionando correctamente";
    }
}
