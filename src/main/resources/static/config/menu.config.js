//菜单配置
define(function () {
    return [
        {
            path: '/',
            name:'首页',
            icoClass:'el-icon-more-outline'
        },
        {
            path: '/general/',
            name:'通用操作',
            icoClass:'el-icon-menu',
            children: [
                {
                    path: '/general/start',
                    componentName: 'start',
                    name:'maxwell启动(停止)'
                },
                {
                    path: '/general/sync',
                    componentName: 'sync',
                    name:'执行表同步'
                }
            ]
        },
        {
            path: '/config/',
            name:'配置管理',
            icoClass:'el-icon-edit',
            children: [
                {
                    path: '/config/config',
                    componentName: 'edit',
                    name:'配置更新'
                },
                {
                    path: '/config/filter',
                    componentName: 'filter',
                    name:'过滤器'
                }
            ]
        },
        {
            path: '/system/',
            name:'系统管理',
            icoClass:'el-icon-setting',
            children: [
                {
                    path: '/system/user',
                    componentName: 'user',
                    name:'用户管理'
                }
            ]
        },
        {
            path: '/help/',
            name:'帮助',
            icoClass:'el-icon-question',
            children: [
                {
                    path: '/help/about',
                    componentName: 'about',
                    name:'关于'
                }
            ]
        }
    ]
});