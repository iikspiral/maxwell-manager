// 定义路由
// 每个路由应该映射一个组件。 其中"component" 可以是
// 通过 Vue.extend() 创建的组件构造器，
// 或者，只是一个组件配置对象。
// 我们晚点再讨论嵌套路由。
// 创建 router 实例，然后传 `routes` 配置

define([
    'vue',
    'vueRouter',
    '/components/home/home.js',
    '/components/general/start/start.js',
    '/components/general/sync/sync.js',
    '/components/config/config/config.js',
    '/components/config/filter/filter.js',
    '/components/help/about/about.js'
], function(Vue, vueRouter,
            home,
            start,
            sync,
            config,
            filter,
            about) {
    'use strict';
    Vue.use(vueRouter);

    var routers =  new vueRouter({
       // mode: 'history',
        //路径配置
        routes:[
            { path: '/',name:'home', component: home},
            { path: '/general/start',name:'start', component: start},
            { path: '/general/sync',name:'sync', component: sync},
            { path: '/config/config',name:'config', component: config},
            { path: '/config/filter',name:'filter', component: filter},
            { path: '/help/about',name:'about', component: about}
        ]
    });

    //加载进度条
    var NProgress = require('nprogress');
    //路由前事件
    routers.beforeEach(function (to,from,next) {
        NProgress.start();
        next();
    });
    //路由后事件
    routers.afterEach(function () {
        NProgress.done();
    });
    return routers;
});