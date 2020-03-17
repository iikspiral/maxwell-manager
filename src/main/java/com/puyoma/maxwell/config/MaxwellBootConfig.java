package com.puyoma.maxwell.config;

import com.puyoma.maxwell.service.MaxwellService;
import com.puyoma.maxwell.util.CacheUtil;
import com.puyoma.maxwell.util.UtilTools;
import com.zendesk.maxwell.Maxwell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    private static final Logger logger = LoggerFactory.getLogger(MaxwellBootConfig.class);

    /**
     * maxwell 启动参数键
     */
    public final static String MAXWELL_START_UP_ARGS = "maxwell.startUp.args";

    /**
     * maxwell 启动状态
     */
    public final static String MAXWELL_STATUS = "maxwell.status";

    @Autowired
    private MaxwellService service;

    @Value("${maxwell.configPath:'classpath:config.properties'}")
    private String configPath;

    @Value("${maxwell.javascriptPath:'classpath:filter.js'}")
    private String javascriptPath;



    @PostConstruct
    void init(){
        //获取配置文件路径
        Map<String,String> commandMap = UtilTools.getCommandMap();
        if(commandMap.containsKey("javascriptPath")){
            configPath = commandMap.get("config");
        }
        //获取js文件路径
        if(commandMap.containsKey("javascriptPath")){
            configPath = commandMap.get("config");
        }

        //缓存初始化
        CacheUtil.init();

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
