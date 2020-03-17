package com.zendesk.maxwell.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.zendesk.maxwell.util
 * @date:2020/3/12
 */
public class Logging {

    public static void setLevel(String level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig= config.getLoggerConfig("com.zendesk.maxwell");
        loggerConfig.setLevel(Level.valueOf(level));
        ctx.updateLoggers();
    }

    public static void setupLogBridging() {
        // Optionally remove existing handlers attached to j.u.l root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();  // (since SLF4J 1.6.5)

        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
        // the initialization phase of your application
        SLF4JBridgeHandler.install();
    }
}
