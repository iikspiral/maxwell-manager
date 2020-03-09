package com.puyoma.maxwell.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.controller
 * @date:2020/2/27
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String test(){
        return "index";
    }
}
