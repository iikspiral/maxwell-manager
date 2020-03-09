//涉及所有接口
define(function () {
    var root = '/api';
    return {
        MAXWELL_URL: root+'/general/maxwell',//包括maxwell 服务启动停止，及服务状态获取
        BOOTSTRAP_URL: root+'/general/bootstrap',//包括maxwell 全量同步表，增删改查
        DATABASES_URL: root+'/general/databases',// 获取的数据库信息
        TABLES_URL: root+'/general/tables',// 数据库表信息
        CONFIG_URL: root+'/config/config',// 配置信息
        METRICS_URL: root+'/monitor/metrics'// 监控信息
    }
});