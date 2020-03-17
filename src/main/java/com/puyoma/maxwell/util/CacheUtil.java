package com.puyoma.maxwell.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.util
 * @date:2020/3/13
 */
public class CacheUtil {
    private static Logger logger = LoggerFactory.getLogger(CacheUtil.class);

    private static Map<String,Object> cacheManage = new ConcurrentHashMap<>();

    static {
        //恢复持久化的map
        try (XMLDecoder xmlDecoder = new XMLDecoder(
                new BufferedInputStream(new FileInputStream("data.xml")))){
            cacheManage = (Map<String,Object>)xmlDecoder.readObject();
        }catch (Exception e){
            logger.warn(e.getMessage());
        }
    }

    public static Object getObjectByname(String name){
        return cacheManage.get(name);
    }

    public static void putCache(String name,Object obj){
        cacheManage.put(name,obj);
        //持久化
        persistenceData();
    }

    public synchronized static void persistenceData(){

        if(!cacheManage.isEmpty()){
            try (XMLEncoder xmlEncoder = new XMLEncoder(
                    new BufferedOutputStream(new FileOutputStream("data.xml")))){
                xmlEncoder.writeObject(cacheManage);
                xmlEncoder.flush();
            }catch (Exception e){
                logger.error("",e);
            }
        }
    }

    public static void init() {
        logger.info("CacheUtil初始化...");
    }
}
