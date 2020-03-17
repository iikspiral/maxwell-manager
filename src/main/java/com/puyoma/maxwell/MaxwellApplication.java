package com.puyoma.maxwell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MaxwellApplication {
    private static final Logger logger = LoggerFactory.getLogger(MaxwellApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MaxwellApplication.class,args);
        logger.info("服务启动完毕...");

    }

}
