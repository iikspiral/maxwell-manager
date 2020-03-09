package com.puyoma.maxwell.controller;

import com.alibaba.fastjson.JSONObject;
import com.puyoma.maxwell.service.MaxwellService;
import com.puyoma.maxwell.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.controller
 * @date:2020/3/8
 */
@RestController
@RequestMapping("api/monitor")
public class MonitorController {

    @Autowired
    private MaxwellService maxwellService;

    /**
     * 获取监控信息
     * @return
     */
    @RequestMapping(value = "/metrics",method = RequestMethod.GET)
    public JsonResult<JSONObject> metrics(){
        return maxwellService.metrics();
    }
}
