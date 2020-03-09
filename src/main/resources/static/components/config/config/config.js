define([
    'vue',
    'axios',
    'api',
    'text!./config.html',
    'css!./config.css',
    'lib/ace/mode-properties'
], function(Vue, axios,Api,tempLate) {
    'use strict';
    return Vue.extend({
        template: tempLate,
        data() {
            return {
                //是否是只读模式
                isReadOnly : true,
                editor:null,
                configVal:''
            }
        },
        mounted() {
             //初始化对象
            //var ace = require('ace');
            this.editor = ace.edit("code");
            //设置风格和语言（更多风格和语言，请到github上相应目录查看）
            this.editor.setTheme("ace/theme/xcode");
            var jsMode = ace.require('ace/mode/properties').Mode;
            this.editor.session.setMode(new jsMode());
            //字体大小
            this.editor.setFontSize(13);
            //设置只读（true时只读，用于展示代码）
            this.editor.setReadOnly(this.isReadOnly);
            //自动换行,设置为off关闭
            this.editor.setOption("wrap", "free")
             //启用提示菜单
            ace.require("ace/ext/language_tools");
            this.editor.setOptions({
                         enableBasicAutocompletion: true,
                         enableSnippets: true,
                         enableLiveAutocompletion: true
                 });


            var _this = this;
            //获取配置文件内容
            axios.get(Api.CONFIG_URL)
                .then(function (response) {
                    if(response.data.success){
                        _this.editor.setValue(response.data.data);
                        _this.configVal = response.data.data;
                    }else {
                        _this.$notify.error({
                            title: '错误',
                            message: response.data.message
                        });
                    }
                });
        },
        methods: {
            /**
             * 保存
             */
            onSubmit(){
                this.isReadOnly = true;
                this.configVal = this.editor.getValue();
            }
        },
        watch:{
            isReadOnly(val){
                if(!val){
                    //设置为编辑模式
                    this.editor.setReadOnly(false);
                }else {
                    this.editor.setReadOnly(true);
                }
            },
            configVal(val,odl){
                var _this = this;
                if(odl){
                    //新增或修改
                    axios.put(Api.CONFIG_URL,{
                        content:val
                    }).then(function (response) {
                        if(response.data.success){
                            _this.$notify.success({
                                title: '成功',
                                message: response.data.message
                            });
                        }else {
                            _this.$notify.error({
                                title: '错误',
                                message: response.data.message
                            });
                        }
                    });
                }
            }
        }
    });
});