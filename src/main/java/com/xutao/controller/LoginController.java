package com.xutao.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xutao
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @GetMapping
    public String login(){
        return "login";
    }
}
