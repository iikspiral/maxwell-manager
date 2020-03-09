// 0. 如果使用模块化机制编程，導入Vue和VueRouter，要调用 Vue.use(VueRouter)

// 1. 定义（路由）组件。
// 可以从其他文件 import 进来
define([
    "vue",
    "/config/menu.config.js",
    "/router/router.js",
    "css!/index.css"
], function(Vue,menus,router) {

    new Vue({
        router:router,
        data: {
            menus: menus,
            breadcrumbs:null,
            currentRoute: '',
            logo_src:'/images/logo.png'
        },
        methods: {
        },
        mounted(){
            this.currentRoute = this._route.path;

        },
        watch: {
            currentRoute(path){
                var flag = false;
                for(var i=0;i<menus.length;i++){
                    var menu = menus[i];
                    //初始化
                    this.breadcrumbs=[];
                    if(menu.children){
                        this.breadcrumbs.push(menu.name);
                        for(var j=0;j<menu.children.length;j++){
                            var item = menu.children[j];
                            if(item.path == path){
                                this.breadcrumbs.push(item.name);
                                flag = true;
                                break;
                            }
                        }
                    }
                    if(flag){
                        break;
                    }
                }
                if(!flag){
                    this.breadcrumbs=[];
                }
            },
            //设置面包屑
            '$route'(to){
                this.currentRoute = to.path;
            }
        }
    }).$mount('#app');
});