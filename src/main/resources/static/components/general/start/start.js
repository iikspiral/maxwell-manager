define([
    'vue',
    'axios',
    'api',
    'text!./start.html',
    'css!./start.css'
], function(Vue, axios,Api,tempLate) {
    'use strict';
    return Vue.extend({
        template: tempLate,
        data() {
            return {
                form:{
                    name:'',
                    value: true
                }
            }
        },
        mounted() {
            var _this = this;
            //服务状态获取
            axios.get(Api.MAXWELL_URL)
                .then(function (response) {
                    if(response.data.data.code){
                        _this.form.value=true;
                    }else {
                        _this.form.value=false;
                    }
                });
        },
        methods: {
            //启动开关
            startEvent(value){
                var _this = this;
                //服务开启
                if(value){
                    axios.put(Api.MAXWELL_URL)
                        .then(function (response) {
                            if(response.data.success){
                                _this.$message({
                                    message: 'maxwell 已开启',
                                    type: 'success'
                                });
                            }else {
                                _this.form.value=false;
                                _this.$message({
                                    message: 'maxwell 开启失败,错误:'+response.message,
                                    type: 'warning'
                                });
                            }
                        });
                }
                //服务关闭
                else {
                    axios.delete(Api.MAXWELL_URL)
                        .then(function (response) {
                            if(response.data.success){
                                _this.$message({
                                    message: 'maxwell 已关闭',
                                    type: 'success'
                                });
                            }else {
                                _this.form.value=true;
                                _this.$message({
                                    message: 'maxwell 关闭失败,错误'+response.message,
                                    type: 'warning'
                                });
                            }
                        });
                }
            }
        }
    });
});