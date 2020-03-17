/**
 * 全局配置文件js,用于引入全部依赖文件包括js、css
 */
require.config({
    //requireJS解析的基准路径，define引入的基准根路径
    baseUrl: "./",
    map: {
        '*': {
            //require 插件用于加载css
            'css': 'lib/require/css.min'
        }
    },
    //js 依赖
    paths: {
        vue: 'lib/vue/vue.min',
        vueRouter: 'lib/vue/vue-router',
        axios: 'lib/plugins/axios.min',
        //饿了么ui框架
        ELEMENT: 'lib/element-ui/lib/index',
        //日期处理工具
        dayjs: 'lib/dayjs/dayjs.min',
        //页面加载进度条
        nprogress: 'lib/nprogress/nprogress.min',
        //api接口配置
        api: 'config/api.config',
        //TempLate 模板插件，用于加载html文件模板
        text: 'lib/require/text',
        //加载ace编辑器
        ace:'lib/ace/ace',
        //图表
        g2:'lib/g2/g2.min',
        sockjs:'lib/socket/sockjs.min'
    },

    //模块依赖配置
    shim: {
        //添加element-ui依赖
        ELEMENT: {
            deps: [
                'vue','css!lib/element-ui/lib/theme-chalk/index.css'
            ]
        },
        nprogress: {
            deps: [
                'vue','css!lib/nprogress/nprogress.min.css'
            ]
        },
        ace:{
            deps:['lib/ace/ext-language_tools.js']
        },
        sockjs:{
            deps:['lib/socket/stomp.min']
        }
    }
});
require([
        'vue',
        'vueRouter',
        'axios',
        'api',
        'dayjs',
        'nprogress',
        'ace',
        'g2',
        'sockjs',
        'ELEMENT','css','text'],
    function (
        Vue,
        vueRouter,
        axios,
        api,
        dayjs,
        nprogress,
        ace,
        g2,
        sockjs,
        ELEMENT) {
    Vue.use(ELEMENT);
    //加载必要模块开始执行
    require(['index.js']);
});