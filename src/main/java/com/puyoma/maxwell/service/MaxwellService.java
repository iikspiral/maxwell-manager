package com.puyoma.maxwell.service;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.djdch.log4j.StaticShutdownCallbackRegistry;
import com.google.api.client.util.ArrayMap;
import com.puyoma.maxwell.dao.BootstrapDao;
import com.puyoma.maxwell.dao.DatabasesDao;
import com.puyoma.maxwell.dao.TablesDao;
import com.puyoma.maxwell.entity.Bootstrap;
import com.puyoma.maxwell.entity.Databases;
import com.puyoma.maxwell.entity.Tables;
import com.puyoma.maxwell.util.JsonResult;
import com.puyoma.maxwell.util.MaxwellApp;
import com.zendesk.maxwell.MaxwellConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.service
 * @date:2020/1/17
 */
@Service
public class MaxwellService {
    private static final Logger logger = LoggerFactory.getLogger(MaxwellService.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY年MM月dd日");
    /**
     * maxwell实例
     */
    private MaxwellApp maxwell;

    /**
     * maxwell启动状态
     */
    private boolean maxwellStatus = false;

    /**
     * 过滤数据库名
     */
    private String[] filterDataBases = {"information_schema","mysql","sys","performance_schema"};

    /**
     * maxwell config
     */
    private MaxwellConfig maxwellConfig;

    @Autowired
    private BootstrapDao bootstrapDao;

    @Autowired
    private DatabasesDao databasesDao;

    @Autowired
    private TablesDao tablesDao;

    public synchronized MaxwellConfig initMaxwellConfig(){
        if(maxwellConfig == null){
            //获取输入变量
            String command = System.getProperty("sun.java.command");
            String[] commands = command.split("--");
            List<String> args = new ArrayList<>();
            for(String str : commands){
                if(str.contains("=")){
                    args.add("--"+str.trim());
                }
            }
            maxwellConfig = new MaxwellConfig(args.toArray(new String[]{}));
        }
        return maxwellConfig;
    }

    /**
     * maxwell 服务启动
     * @return
     */
    public boolean start(){
        try{
            logger.info("maxwell 正在启动中...");
            initMaxwellConfig();
            maxwell = new MaxwellApp(maxwellConfig);
            maxwellStatus = true;
            if ( maxwellConfig.log_level != null ){
                Runtime.getRuntime().addShutdownHook(new Thread(()->{
                    maxwell.terminate();
                    StaticShutdownCallbackRegistry.invoke();
                }));
            }
            maxwell.start();
            logger.info("maxwell 已经启动完成!");

        }catch (Exception e){
            maxwellStatus = false;
            logger.error("",e);
        }
        return maxwellStatus;
    }

    public void stop(){
        logger.info("maxwell 正在执行关闭中...");
        maxwell.stop();
        maxwellConfig = null;
        maxwellStatus = false;
        logger.info("maxwell 已关闭!");
    }

    public boolean getMaxwellStatus() {
        return maxwellStatus;
    }


    /**
     * 全量表查询
     * @param
     * @return
     */
    public Map<String,Map<String,List<Bootstrap>>> queryBootstrap(String startTime,String endTime,String dbName,String tableNames){

        Map<String,Object> paramMap = new ArrayMap<>();
        paramMap.put("startTime",startTime);
        paramMap.put("endTime",endTime);
        paramMap.put("databaseName",dbName);
        paramMap.put("tableName",tableNames);
        List<Bootstrap> list = bootstrapDao.queryAll(paramMap);
        Map<String,Map<String,List<Bootstrap>>> dateMap = new LinkedHashMap();
        //组合分组数据
        for(Bootstrap bean : list){
            Date creatDate = bean.getCreatedAt();
            String dataBaseName = bean.getDatabaseName();
            //创建时间为空
            if(creatDate == null){
                //判断是否存在没有日期的数据对象，没有则新增
                if(!dateMap.containsKey("")){
                    Map<String,List<Bootstrap>> map = new HashMap<>();
                    map.put(dataBaseName,new ArrayList<>());
                    dateMap.put("",map);
                }
                //判断是否存在当前数据库的数据，没有则新增
                if(!dateMap.get("").containsKey(dataBaseName)){
                    dateMap.get("").put(dataBaseName,new ArrayList<>());
                }
                dateMap.get("").get(dataBaseName).add(bean);
                continue;
            }
            //创建时间非空情况下
            String dateStr = sdf.format(creatDate);
            //判断是否存在没有日期的数据对象，没有则新增
            if(!dateMap.containsKey(dateStr)){
                Map<String,List<Bootstrap>> map = new HashMap<>();
                map.put(dataBaseName,new ArrayList<>());
                dateMap.put(dateStr,map);
            }
            //判断是否存在当前数据库的数据，没有则新增
            if(!dateMap.get(dateStr).containsKey(dataBaseName)){
                dateMap.get(dateStr).put(dataBaseName,new ArrayList<>());
            }
            dateMap.get(dateStr).get(dataBaseName).add(bean);
        }
        return dateMap;
    }

    /**
     * 添加同步表信息
     * @param bootstrap
     * @return
     */
    public boolean addBootstrap(Bootstrap bootstrap){

        //设置创建时间
        bootstrap.setCreatedAt(new Date());
        //默认值
        bootstrap.setInsertedRows(0L);
        bootstrap.setTotalRows(0L);
        bootstrap.setBinlogPosition(0);
        bootstrap.setClientId("maxwell01");
        return bootstrapDao.insert(bootstrap) >0 ? true : false;
    }

    /**
     * 删除同步表信息
     * @param id
     * @return
     */
    public boolean deleteBootstrap(String id){

        return bootstrapDao
                .deleteById(Integer.valueOf(id)) >0 ? true : false;
    }

    /**
     * 编辑同步表信息
     * @param bootstrap
     * @return
     */
    public boolean editBootstrap(Bootstrap bootstrap){

        //当设置为未完成时，影响记录数重置
        if(bootstrap.getIsComplete() == 0){
            bootstrap.setInsertedRows(0L);
        }
        return bootstrapDao.update(bootstrap) >0 ? true : false;
    }

    /**
     * 获取当前数据库实例
     * @return
     */
    public List<Databases> queryDatabases(){
        List<Databases> list = databasesDao.queryAllByLimit(0,Byte.MAX_VALUE);
        //todo 过滤
        return  list
                .stream()
                .filter(d->{
                    String dbName = d.getName();
                    if(filterDataBases != null && filterDataBases.length > 0){
                        for(String name : filterDataBases){
                            if(name.equals(dbName)){
                                return false;
                            }
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取当前数据库实例
     * @return
     */
    public List<Tables> queryTables(String dbName){

        //先查询db 并获取其ID
        Databases queryDb = new Databases();
        queryDb.setName(dbName);
        List<Databases> list = databasesDao.queryAll(queryDb);

        List<Tables> returnList = new ArrayList<>();
        if(list != null && !list.isEmpty()){
            queryDb = list.get(0);
            Tables queryTable = new Tables();
            queryTable.setDatabaseId(queryDb.getId());
            returnList = tablesDao.queryAll(queryTable);
        }

        //todo 过滤
        return  returnList;
    }


    /**
     * 系统统计信息
     */
    public JsonResult<JSONObject> metrics(){

        if(maxwellConfig == null
                || maxwellConfig.metricRegistry == null){
            return JsonResult.failure("请先启动maxwell服务...");
        }
        MetricRegistry metricRegistry = maxwellConfig.metricRegistry;
        JSONObject js = new JSONObject();
        js.put("version","4.0.0");

        //gauges
        Map<String,Object> gaugesMap = new HashMap<>();
        SortedMap<String, Gauge> gauges = metricRegistry.getGauges();
        for(String key : gauges.keySet()){
            gaugesMap.put(key,gauges.get(key));
        }
        js.put("gauges",gaugesMap);

        //counters
        Map<String,Object> countersMap = new HashMap<>();
        SortedMap<String, Counter> counters = metricRegistry.getCounters();
        for(String key : counters.keySet()){
            countersMap.put(key,counters.get(key));
        }
        js.put("counters",countersMap);


        //histograms
        Map<String,Object> histogramsMap = new HashMap<>();
        SortedMap<String, Histogram> histograms = metricRegistry.getHistograms();
        for(String key : histograms.keySet()){
            histogramsMap.put(key,histograms.get(key));
        }
        js.put("histograms",histogramsMap);

        //meters
        Map<String,Object> metersMap = new HashMap<>();
        SortedMap<String, Meter> meters = metricRegistry.getMeters();
        for(String key : meters.keySet()){
            metersMap.put(key,meters.get(key));
        }
        js.put("meters",metersMap);

        //timers
        Map<String,Object> timersMap = new HashMap<>();
        SortedMap<String, Timer> timers = metricRegistry.getTimers();
        for(String key : timers.keySet()){
            timersMap.put(key,timers.get(key));
        }
        js.put("timers",timersMap);

        return new JsonResult<>(js);
    }
}
