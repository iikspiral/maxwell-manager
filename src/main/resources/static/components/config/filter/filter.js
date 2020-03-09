define([
    'vue',
    'axios',
    'api',
    'text!./filter.html',
    'css!./filter.css'
], function(Vue, axios,Api,tempLate) {
    'use strict';
    return Vue.extend({
        template: tempLate,
        data() {
            return {
                activeName: 'second',
                tableData: [{
                    date: '2016-05-02',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1518 弄'
                }, {
                    date: '2016-05-04',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1517 弄'
                }, {
                    date: '2016-05-01',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1519 弄'
                }, {
                    date: '2016-05-03',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1516 弄'
                }]
            }
        },
        mounted() {
             //初始化对象
            // var ace = require('ace');
             var editor = ace.edit("code");

             //设置风格和语言（更多风格和语言，请到github上相应目录查看）
             editor.setTheme("ace/theme/xcode");
             var jsMode = ace.require('ace/mode/javascript').Mode;
             editor.session.setMode(new jsMode());

             //字体大小
             editor.setFontSize(13);

             //设置只读（true时只读，用于展示代码）
             editor.setReadOnly(false);

             //自动换行,设置为off关闭
             editor.setOption("wrap", "free")

             //启用提示菜单
             ace.require("ace/ext/language_tools");
             editor.setOptions({
                         enableBasicAutocompletion: true,
                         enableSnippets: true,
                         enableLiveAutocompletion: true
                 });
            editor.setValue('test\n' +
                '   kkj');
        },
        methods: {
            handleClick(tab, event) {
                console.log(tab, event);
            }
        }
    });
});