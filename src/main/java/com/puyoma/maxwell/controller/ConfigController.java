package com.puyoma.maxwell.controller;

import com.puyoma.maxwell.service.ConfigService;
import com.puyoma.maxwell.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.controller
 * @date:2020/3/7
 */
@RestController
@RequestMapping("api/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * 获取配置文件内容
     * @return
     */
    @RequestMapping(value = "/config",method = RequestMethod.GET)
    public JsonResult<String> getConfig(){
        return configService.getConfig();
    }

    /**
     * 修改配置文件内容
     * @return
     */
    @RequestMapping(value = "/config",method = RequestMethod.PUT)
    public JsonResult<String> editConfig(@RequestBody Map<String,Object> map){
        return configService.editConfig(map.get("content").toString());
    }
}
