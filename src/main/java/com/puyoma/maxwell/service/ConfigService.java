package com.puyoma.maxwell.service;

import com.puyoma.maxwell.config.MaxwellBootConfig;
import com.puyoma.maxwell.util.JsonResult;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.service
 * @date:2020/3/7
 */
@Service
public class ConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    @Autowired
    private MaxwellBootConfig maxwellBootConfig;


    public JsonResult<String> getConfig(){
        String path = maxwellBootConfig.getConfigPath();
        if(Strings.isBlank(path)){
            return JsonResult.failure("请先配置config.properties 地址...");
        }
        StringBuffer sbf = new StringBuffer();
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr+"\n");
            }
        }catch (Exception e){
            logger.error("",e);
        }
        return new JsonResult<>(sbf.toString());
    }

    public JsonResult<String> editConfig(String content){
        String filePath = maxwellBootConfig.getConfigPath();
        if(Strings.isBlank(filePath)){
            return JsonResult.failure("请先配置config.properties 地址...");
        }


        String message = "已修改成功！";
        File file = new File(filePath);
        if(!file.exists()){
            message = "已成功新增配置文件，文件路径为："+filePath;
        }

        try(FileOutputStream fos =
                    new FileOutputStream(new File(filePath))) {
            fos.write(content.getBytes("UTF-8"));
        }catch (Exception e){
            JsonResult.failure(e.getMessage());
            logger.error("",e);
        }
        return JsonResult.success(message);
    }

}
