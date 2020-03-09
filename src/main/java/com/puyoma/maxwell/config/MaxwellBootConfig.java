package com.puyoma.maxwell.config;

import com.google.api.client.util.Value;
import com.puyoma.maxwell.service.MaxwellService;
import com.puyoma.maxwell.util.UtilTools;
import com.zendesk.maxwell.Maxwell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.config
 * @date:2020/1/17
 */
@Configuration
@ConditionalOnClass(Maxwell.class)
public class MaxwellBootConfig{
    static final Logger logger = LoggerFactory.getLogger(MaxwellBootConfig.class);

    @Autowired
    private MaxwellService service;

    @Value("${maxwell.configPath:'classpath:config.properties'}")
    private String configPath;



    @PostConstruct
    void init(){
        //获取配置文件路径
        Map<String,String> commandMap = UtilTools.getCommandMap();
        if(commandMap.containsKey("config")){
            configPath = commandMap.get("config");
        }

        //初始化服务启动
    //    service.start();
    }

    @PreDestroy
    void destroy(){
        //关闭销毁
        logger.info("destroy opt...");
        if(service.getMaxwellStatus()){
            service.stop();
        }
    }


    public String getConfigPath() {
        return configPath;
    }
}
