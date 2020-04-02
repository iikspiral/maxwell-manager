package com.puyoma.maxwell.listener;

import com.puyoma.maxwell.config.MaxwellBootConfig;
import com.puyoma.maxwell.service.MaxwellService;
import com.puyoma.maxwell.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.listener
 * @date:2020/4/2
 */
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {
    private static Logger logger = LoggerFactory.getLogger(ApplicationReadyEventListener.class);

    @Autowired
    private MaxwellService service;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Object obj = CacheUtil.getObjectByname(MaxwellBootConfig.MAXWELL_STATUS);
        if(null != obj){
            if((boolean)obj){
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            service.start();
                        }catch (Exception var1){
                            logger.error("",var1);
                        }
                    }
                }.start();
            }

        }
    }
}