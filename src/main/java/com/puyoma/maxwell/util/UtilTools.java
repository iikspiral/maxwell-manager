package com.puyoma.maxwell.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.util
 * @date:2020/3/7
 */
public class UtilTools {

    public static Map<String,String> getCommandMap(){
        //获取输入变量
        String command = System.getProperty("sun.java.command");
        Map<String,String> commandMap = new HashMap<>();
        for ( String opt : command.split("--") ) {
            String[] valueKeySplit = opt.trim().split("=", 2);
            if (valueKeySplit.length == 2) {
                commandMap.put(valueKeySplit[0], valueKeySplit[1]);
            }
        }
        return commandMap;
    }
}
