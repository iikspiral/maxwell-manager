package com.puyoma.maxwell.log;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * 自定义实现log4j2的输出源
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.log
 * @date:2020/3/15
 */

@Plugin(name = "Log4j2Appender", category = "Core", elementType = "appender", printObject = true)
public final class Log4j2Appender extends AbstractAppender {

    /** 日志级别大于等于此级别及以上会进行判断错误。默认：ERROR */
    private String failedOnLogLevel;
    /** 指定时间内，出现多少次该日志级别，会被认为是错误。默认：10 */
    private Integer failedOnLogLevelCount;
    /** 该日志级别以上持续出现多长时间，会被认为是错误。默认:30000 */
    private Integer failedOnLogLevelInMisSecond;
    /** 当连续小于该日志级别多长时间后，恢复为正常状态。默认：120000 */
    private Integer recoveryOnLessLogLevelInMisSecond;

    protected Log4j2Appender(String name, Filter filter, Layout<? extends Serializable> layout,
                                 String failedOnLogLevel,
                                 Integer failedOnLogLevelCount,
                                 Integer failedOnLogLevelInMisSecond,
                                 Integer recoveryOnLessLogLevelInMisSecond) {
        super(name, filter, layout);
        this.failedOnLogLevel = failedOnLogLevel;
        this.failedOnLogLevelCount = failedOnLogLevelCount;
        this.failedOnLogLevelInMisSecond = failedOnLogLevelInMisSecond;
        this.recoveryOnLessLogLevelInMisSecond = recoveryOnLessLogLevelInMisSecond;
    }

    @Override
    public void append(LogEvent logEvent) {
        //此处省略告警过滤统计代码。
        // .....
        StringBuilder exception = new StringBuilder();
        ThrowableProxy throwableProxy = logEvent.getThrownProxy();
        if(throwableProxy!=null){
            exception.append("<span class='excehtext'>"+throwableProxy.getExtendedStackTraceAsString()+" "+throwableProxy.getMessage()+"</span></br>");

            StackTraceElement[] elements = throwableProxy.getStackTrace();
            for(int i=0; i<elements.length;i++){
                exception.append("<span class='excetext'>"+elements[i].toString()+"</span></br>");
            }
        }

        LoggerQueue.getInstance().push(new LoggerMessage(
                logEvent.getMessage().getFormattedMessage(),
                DateFormat.getDateTimeInstance().format(new Date(logEvent.getTimeMillis())),
                logEvent.getThreadName(),
                logEvent.getLoggerName(),
                logEvent.getLevel().toString(),
                exception.toString(),
                ""
        ));
    }

    /**
     * log4j2 使用 appender 插件工厂，因此传参可以直接通过 PluginAttribute 注解注入
     */
    @PluginFactory
    public static Log4j2Appender
        createAppender(@PluginAttribute("name") String name,
                        @PluginElement("Filter") final Filter filter,
                        @PluginElement("Layout") Layout<? extends Serializable> layout,
                        @PluginAttribute("failedOnLogLevel") String failedOnLogLevel,
                        @PluginAttribute("failedOnLogLevelCount") Integer failedOnLogLevelCount,
                        @PluginAttribute("failedOnLogLevelInMisSecond") Integer failedOnLogLevelInMisSecond,
                        @PluginAttribute("recoveryOnLessLogLevelInMisSecond") Integer recoveryOnLessLogLevelInMisSecond) {
        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        if (failedOnLogLevel == null) {
            failedOnLogLevel = "ERROR";
        }
        if (failedOnLogLevelCount == null) {
            failedOnLogLevelCount = 10;
        }
        if (failedOnLogLevelInMisSecond == null) {
            failedOnLogLevelInMisSecond = 30000;
        }
        if (recoveryOnLessLogLevelInMisSecond == null) {
            recoveryOnLessLogLevelInMisSecond = 120000;
        }
        return new Log4j2Appender(name, filter, layout, failedOnLogLevel, failedOnLogLevelCount, failedOnLogLevelInMisSecond, recoveryOnLessLogLevelInMisSecond);
    }
}