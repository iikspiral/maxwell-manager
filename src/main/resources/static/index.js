// 1. 定义（路由）组件。
// 可以从其他文件 import 进来
define([
    "vue",
    "config/menu.config.js",
    "router/router.js",
    "css!/index.css"
], function(Vue,menus,router) {

    new Vue({
        router:router,
        data: {
            menus: menus,
            logo_src:'/images/logo.png',
            homeLabel:'首页',
            logDialogVisible: false,
            client:null,
            logs:[],
            logRows:1000
        },
        methods: {
            logHandleOpen(){
                var _this = this;
                _this.logs = [];
                if(!_this.client){
                    _this.client = Stomp.over(
                        new SockJS('/websocket'));
                    _this.client.debug=null;
                    _this.client.connect({},
                    //连接成功
                    function () {
                        _this.client.subscribe('/topic/pullLogger',function (event) {
                            _this.logs.push(JSON.parse(event.body));
                            if(_this.logs.length > _this.logRows){
                                _this.logs.shift();
                            }
                        });
                    },
                    //失去连接
                    function () {
                        _this.client = null;
                        console.log('Disconnected');
                    });
                }
            },
            logHandleClose(){
                if (this.client) {
                    this.client.disconnect();
                    this.client = null;
                }
                console.log("Disconnected");
            }
        },
        computed:{
            /**
             * 当前路径变化，用于设置面包屑
             * @param path
             */
            breadcrumbs(){
                let breadcrumbs = [];
                var flag = false;
                let path = this.$route.path;
                for(var i=0;i < menus.length;i++){
                    var menu = menus[i];
                    //初始化
                    breadcrumbs=[];
                    if(menu.children){
                        breadcrumbs.push(menu.name);
                        for(var j=0;j<menu.children.length;j++){
                            var item = menu.children[j];
                            if(item.path == path){
                                breadcrumbs.push(item.name);
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
                    breadcrumbs=[];
                }
                return breadcrumbs;
            }
        },
        watch: {
        }
    }).$mount('#app');
});