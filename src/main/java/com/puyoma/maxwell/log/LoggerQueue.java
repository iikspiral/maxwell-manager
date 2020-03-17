package com.puyoma.maxwell.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.log
 * @date:2020/3/15
 */
public class LoggerQueue {
    private static Logger logger = LoggerFactory.getLogger(LoggerQueue.class);
    //队列大小
    private static final int QUEUE_MAX_SIZE = 10000;
    //阻塞队列
    private BlockingQueue<LoggerMessage> blockingQueue = new LinkedBlockingQueue<>(QUEUE_MAX_SIZE);

    private LoggerQueue() {
    }

    private static class Holder{
        private final static LoggerQueue instance = new LoggerQueue();
    }
    public static synchronized LoggerQueue getInstance() {
        return Holder.instance;
    }

    /**
     * 消息入队
     * @param log
     * @return
     */
    public boolean push(LoggerMessage log) {
        return this.blockingQueue.add(log);//队列满了就抛出异常，不阻塞
    }

    /**
     * 消息出队
     * @return
     */
    public LoggerMessage poll() {
        LoggerMessage result = null;
        try {
            result = this.blockingQueue.take();
        } catch (InterruptedException e) {
            logger.error("",e);
        }
        return result;
    }



}
