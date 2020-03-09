package com.puyoma.maxwell.controller;

import com.alibaba.fastjson.JSONObject;
import com.puyoma.maxwell.entity.Bootstrap;
import com.puyoma.maxwell.entity.Databases;
import com.puyoma.maxwell.entity.Tables;
import com.puyoma.maxwell.service.MaxwellService;
import com.puyoma.maxwell.util.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.controller
 * @date:2020/3/2
 */

@RestController
@RequestMapping("/api/general")
public class GeneralController {
    private static final Logger logger = LoggerFactory.getLogger(GeneralController.class);

    @Autowired
    private MaxwellService maxwellService;

    /**
     * 服务启动
     * @return
     */
    @RequestMapping(value = "/maxwell",method = RequestMethod.PUT)
    public JsonResult<String> maxwellPut(){
        try{
            //启动maxwell
            maxwellService.start();
            return JsonResult.success("启动成功");
        }catch (Exception e){
            logger.error("",e);
            return JsonResult.failure(e.getMessage());
        }
    }

    /**
     * 服务停止
     * @return
     */
    @RequestMapping(value = "/maxwell",method = RequestMethod.DELETE)
    public JsonResult<String> maxwellDelete(){
        try{
            //todo
            maxwellService.stop();
            return JsonResult.success("服务关闭成功");
        }catch (Exception e){
            logger.error("",e);
            return JsonResult.failure(e.getMessage());
        }
    }

    /**
     * 服务状态获取
     * @return
     */
    @RequestMapping(value = "/maxwell",method = RequestMethod.GET)
    public JsonResult<JSONObject> maxwellGet(){

        JSONObject js = new JSONObject();
        if(maxwellService.getMaxwellStatus()){
            js.put("code",Boolean.TRUE);
            return new JsonResult<>(js);
        }
        js.put("code",Boolean.FALSE);
        return new JsonResult<>(js);
    }

    /**
     * 查询Bootstrap，信息
     * @return
     */
    @RequestMapping(value = "/bootstrap",method = RequestMethod.GET)
    public JsonResult<Map<String,Map<String,List<Bootstrap>>>> queryBootstrap(
            @RequestParam(name = "startTime",required=false) String startTime,
                @RequestParam(name = "endTime",required=false) String endTime,
                @RequestParam(name = "dbName",required=false) String dbName,@RequestParam(name = "tableNames",required=false) String tableNames){

        Map<String,Map<String,List<Bootstrap>>> dataMap =
                maxwellService.queryBootstrap(startTime,endTime,dbName,tableNames);
        return new JsonResult<>(dataMap);
    }

    /**
     * 添加同步表信息
     * @return
     */
    @RequestMapping(value = "/bootstrap",method = RequestMethod.POST)
    public JsonResult addBootstrap(@RequestBody Bootstrap bootstrap){

        return maxwellService.addBootstrap(bootstrap) ?
                JsonResult.success("") : JsonResult.failure("失败！");
    }

    /**
     * 删除同步表信息
     * @return
     */
    @RequestMapping(value = "/bootstrap",method = RequestMethod.DELETE)
    public JsonResult deleteBootstrap(@RequestParam(name = "id") String id){

        return maxwellService.deleteBootstrap(id) ?
                JsonResult.success("") : JsonResult.failure("失败！");
    }

    /**
     * 编辑同步表信息
     * @return
     */
    @RequestMapping(value = "/bootstrap",method = RequestMethod.PUT)
    public JsonResult editBootstrap(@RequestBody Bootstrap bootstrap){

        return maxwellService.editBootstrap(bootstrap) ?
                JsonResult.success("") : JsonResult.failure("失败！");
    }


    /**
     * 获取数据库实例信息
     * @return
     */
    @RequestMapping(value = "/databases",method = RequestMethod.GET)
    public JsonResult<List<Databases>> queryDatabases(){
        List<Databases> list = maxwellService.queryDatabases();
        return new JsonResult<>(list);
    }

    /**
     * 获取对应数据库的表信息
     * @param dbName
     * @return
     */
    @RequestMapping(value = "/tables",method = RequestMethod.GET)
    public JsonResult<List<Tables>> queryTables(@RequestParam(name = "dbName") String dbName){
        List<Tables> list = maxwellService.queryTables(dbName);
        return new JsonResult<>(list);
    }

}
