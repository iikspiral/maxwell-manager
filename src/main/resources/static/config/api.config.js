//涉及所有接口
define(function () {
    let root = '/api';
    return {
        //包括maxwell 服务启动停止，及服务状态获取
        MAXWELL_URL: root+'/general/maxwell',
        //包括maxwell 全量同步表，增删改查
        BOOTSTRAP_URL: root+'/general/bootstrap',
        //获取的数据库信息
        DATABASES_URL: root+'/general/databases',
        //数据库表信息
        TABLES_URL: root+'/general/tables',
        //配置信息
        CONFIG_URL: root+'/config/config',
        //监控信息
        METRICS_URL: root+'/monitor/metrics'
    }
});