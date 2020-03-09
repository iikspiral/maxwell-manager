
//数据处理
define(function () {
    return {
        //菜单配置
        menu:[
            {
                path: '/general/',
                name:'通用操作',
                icoClass:'el-icon-orange',
                children: [
                    {
                        path: 'start',
                        componentName: 'start',
                        name:'maxwell启动(停止)'
                    },
                    {
                        path: 'sync',
                        componentName: 'sync',
                        name:'执行表同步'
                    }
                ]
            },
            {
                path: '/config/',
                name:'配置管理',
                icoClass:'el-icon-cpu',
                children: [
                    {
                        path: 'edit',
                        componentName: 'edit',
                        name:'配置更新'
                    }
                ]
            },
            {
                path: '/system/',
                name:'系统管理',
                icoClass:'el-icon-setting',
                children: [
                    {
                        path: 'user',
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
                        path: 'about',
                        componentName: 'about',
                        name:'关于'
                    }
                ]
            }
        ]
    }
});