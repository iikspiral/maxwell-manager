require.config({
    baseUrl: "./", //reqireJS解析的基准路径，define引入的基准根路径
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
        ELEMENT: 'lib/element-ui/lib/index',
        dayjs: 'lib/dayjs/dayjs.min',
        nprogress: 'lib/nprogress/nprogress',
        //api接口配置
        api: 'config/api.config',
        //TempLate 模板插件，用于加载html文件模板
        text: 'lib/require/text',
        //加载ace编辑器
        ace:'lib/ace/ace',
        //图表
        g2:'lib/g2/g2.min'
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
                'vue','css!lib/nprogress/nprogress.css'
            ]
        },
        ace:{
            deps:['lib/ace/ext-language_tools']
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
        ELEMENT) {
    Vue.use(ELEMENT);
    //加载必要模块开始执行
    require(['/index.js']);
});