define([
    'vue',
    'axios',
    'api',
    'text!./sync.html',
    'css!./sync.css'
], function(Vue,axios,Api,tempLate) {
    'use strict';
    //加载进度条
    var NProgress = require('nprogress');
    //获取时间对象dayjs
    var dayjs = require('dayjs');
    return Vue.extend({
        template: tempLate,
        data() {
            return {
                isEdit:false,   //是否编辑模式
                currentRow:{},  //编辑模式下当前点中行数据
                intervalId: null,//定时任务ID
                isRefresh:false,//是否刷新
                noteTitle:'',   //提示信息内容
                searchForm: {   //查询form数据
                    dbName: '',
                    tableNames: '',
                    creatDate: []
                },
                searchPopoverVisible:false,
                editDialogVisible:false,
                editForm:{
                    dbName: '',
                    tableNames: '',
                    isComplete: '0',
                    whereClause: '',
                    lableDbName: '',
                    lableTableName: ''
                },
                dataBases:[],
                tables:[],
                viewDateDatas:{}
            }
        },
        mounted() {
            //设置查询时间
            this.searchForm.creatDate.push(dayjs().subtract(30, 'day'));
            this.searchForm.creatDate.push(dayjs());
            //初始化
            this.queryBootstrap();
            //开启任务
            this.isRefresh = true;
        },
        methods: {
            /**
             * 查询
             */
            searchOnSubmit() {
                //设置弹出框隐藏
                this.searchPopoverVisible = false;
                this.queryBootstrap();
            },
            /**
             * 查询Bootstrap历史信息
             */
            queryBootstrap(){
                NProgress.start();
                var _this = this;
                var data = {};

                this.noteTitle = '';
                if(this.searchForm.dbName){
                    data.dbName = this.searchForm.dbName;
                    this.noteTitle = '数据库名['+data.dbName+'] ';
                }
                if(this.searchForm.tableNames){
                    data.tableNames = this.searchForm.tableNames;
                    this.noteTitle +='  数据表名['+data.tableNames+'] ';
                }

                var dates = this.searchForm.creatDate;
                if(dates && dates.length > 0){
                    data.startTime = dayjs(dates[0]).format('YYYY-MM-DD');
                    data.endTime = dayjs(dates[1]).format('YYYY-MM-DD');
                    this.noteTitle +='  时间['+data.startTime+' 至 '+data.endTime+']';
                }

                if(this.noteTitle){
                    this.noteTitle =this.noteTitle;
                }

                axios.get(Api.BOOTSTRAP_URL, {
                        params:data
                    }).then(function (response) {
                        if(response.data.data){
                            _this.viewDateDatas = response.data.data;
                        }
                        NProgress.done();
                    });
            },

            /**
             * 修改或创建提交
             */
            onSubmit(){
                var _this = this;
                if(!this.isEdit){
                    axios.post(Api.BOOTSTRAP_URL,{
                            databaseName: _this.editForm.dbName,
                            tableName: _this.editForm.tableNames,
                            whereClause: _this.editForm.whereClause,
                            isComplete: _this.editForm.isComplete
                    }).then(function (response) {
                        if(response.data.success){
                            _this.$notify({
                                title: '成功',
                                message: '新增同步表成功',
                                type: 'success'
                            });
                            _this.queryBootstrap();
                            _this.editDialogVisible=false;
                        }
                    });
                    return;
                }
                //编辑提交
                axios.put(Api.BOOTSTRAP_URL,{
                    id:_this.currentRow.id,
                    isComplete: _this.editForm.isComplete
                }).then(function (response) {
                    if(response.data.success){
                        _this.queryBootstrap();
                        _this.editDialogVisible=false;
                    }
                });
            },

            /**
             * 编辑
             * @param row
             */
            editBootstrap(row){
                var _this = this;
                //开启编辑模式
                this.isEdit = true;
                this.currentRow = row;
                this.editDialogVisible = true;
                this.editForm.lableDbName = row.databaseName;
                this.editForm.lableTableName = row.tableName;
                this.editForm.whereClause = row.whereClause;
                this.editForm.isComplete = row.isComplete+'';
            },

            /**
             * 删除
             * @param row
             */
            deleteBootstrap(row){
                var _this = this;
                this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    axios.delete(Api.BOOTSTRAP_URL,{
                        params:{
                            id:row.id
                        }
                    }).then(function (response) {
                        if(response.data.success){
                            _this.$notify({
                                title: '成功',
                                message: '删除成功',
                                type: 'success'
                            });
                            _this.queryBootstrap();
                        }
                    });
                });
            },

            /**
             * 获取数据库信息
             */
            requestDataBases(){
                var _this = this;
                axios.get(Api.DATABASES_URL)
                    .then(function (response) {
                        _this.dataBases = response.data.data;
                });
            },

            /**
             * 获取数据库表信息
             */
            requestTables(dbName){
                var _this = this;
                axios.get(Api.TABLES_URL,{
                    params:{
                        dbName:dbName
                    }
                }).then(function (response) {
                        _this.tables = response.data.data;
                });
            },

            /**
             * 2分钟字段刷新任务
             */
            startRefreshEvent(){
                var _this = this;
                return setInterval(function(){
                    _this.queryBootstrap();
                },1000*60*2);
            }
        },
        watch:{
            //提示信息关闭
            noteTitle(val){
                if(!val){
                    this.searchForm.dbName='';
                    this.searchForm.tableNames='';
                    this.searchForm.creatDate=[];
                    this.queryBootstrap();
                }
            },

            //查询
            searchPopoverVisible(val) {
                if(val){
                    this.requestDataBases();
                }
            },

            //搜索框 数据库选择事件
            'searchForm.dbName':function (val) {
                this.searchForm.tableNames = '';
                this.requestTables(val);
            },

            //editDialog
            editDialogVisible(val){
                //数据清理
                if(!val){
                    this.editForm.dbName='';
                    this.editForm.tableNames='';
                    this.editForm.whereClause='';
                    this.editForm.isComplete='0';
                    if(this.isEdit){
                        this.isEdit = false;
                    }
                }else if(!this.isEdit){
                    this.requestDataBases();
                }
            },

            //select event
            'editForm.dbName':function (val) {
                this.editForm.tableNames = '';
                this.requestTables(val);
            },

            //2分钟定时任务
            isRefresh(val){
                if(val){
                    this.intervalId = this.startRefreshEvent();
                }else {
                    clearInterval(this.intervalId);
                }
            }
        }
    });
});