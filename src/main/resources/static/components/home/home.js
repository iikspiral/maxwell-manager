define([
    'vue',
    'axios',
    'g2',
    'api',
    'text!./home.html',
    'css!./home.css'
], function(Vue, axios,G2,Api,tempLate) {
    'use strict';
    var NProgress = require('nprogress');
    return Vue.extend({
        template: tempLate,
        data() {
            return {
                chart:null,
                kafkaMonitorChart:null,
                memoryUsageMonitorChart:null,
                memoryMonitorChart:null,
                counters:[
                    { country: '成功发送到kafka的消息数量', population: 0 },
                    { country: '已处理的binlog行数', population: 0 },
                    { country: '发送失败的消息数量', population: 0 }
                ],
                memoryUsageData:[
                    { type: '内存使用', value: 0 },
                    { type: '剩余内存', value: 0 }
                ],
                memoryData:[
                    { type: '最大内存', value: 0 },
                    { type: '可以使用内存', value: 0 },
                    { type: '已经使用内存', value: 0 }
                ]
            }
        },
        mounted() {
            //kafka监控信息
            this.initKafkaMonitor();

            //内存使用信息
            this.initMemoryUsageMonitor();

            //内存使用信息
            this.initMemoryMonitor();

            this.getMonitorData();
        },
        methods: {
            initMemoryMonitor(){
                const chart = new G2.Chart({
                    container: 'memoryContainer',
                    autoFit: true,
                    height: 245,
                });
                chart.data(this.memoryData);
                chart.scale('population', {
                    alias: '内存(兆)',
                    nice: true
                });
                chart.axis('type', {
                    tickLine: null,
                });

                chart.axis('value', {
                    label: {
                        formatter: (val) => {
                            return val+'v';
                        },
                    },
                });

                chart.tooltip({
                    showMarkers: false,
                });
                chart.interaction('element-active');

                chart.legend(false);
                chart
                    .interval()
                    .position('type*value')
                    .label('value', {
                        content: (originData) => {
                            return originData.value + 'M';
                        },
                        offset: 10,
                    });

                chart.render();
                this.memoryMonitorChart = chart;

            },

            /**
             * 初始化kafka监控信息
             */
            initKafkaMonitor(){
                const chart = new G2.Chart({
                    container: 'kafkaMonitorContainer',
                    autoFit: true,
                    height: 200,
                });

                chart.data(this.counters);
                chart.scale('population', { nice: true });
                chart.coordinate().transpose();
                chart.tooltip({
                    showMarkers: false
                });
                chart.interaction('active-region');
                chart.interval().position('country*population');
                chart.render();
                this.kafkaMonitorChart = chart;
            },

            /**
             * 初始化内存信息
             */
            initMemoryUsageMonitor(){
                const chart = new G2.Chart({
                    container: 'memoryUsageContainer',
                    autoFit: true,
                    height: 250,
                });
                chart.data(this.memoryUsageData);
                chart.legend(false);

                chart.scale('value', {
                    alias: '内存(兆)',
                    nice: true
                });
                chart.coordinate('theta', {
                    radius: 0.75,
                });
                chart.tooltip({
                    showMarkers: false
                });

                const interval = chart
                    .interval()
                    .adjust('stack')
                    .position('value')
                    .color('type', ['#063d8a','#1770d6'])
                    .style({ opacity: 0.4 })
                    .label('type', (val) => {
                        const opacity = val === '内存使用' ? 1 : 0.5;
                        return {
                            offset: -30,
                            style: {
                                opacity,
                                fill: 'white',
                                fontSize: 12,
                                shadowBlur: 2,
                                shadowColor: 'rgba(0, 0, 0, .45)',
                            },
                            content: (obj) => {
                                return obj.type + '\n' + obj.value + '%';
                            },
                        };
                    });

                chart.interaction('element-single-selected');
                chart.render();
                this.memoryUsageMonitorChart = chart;
                // 默认第一个选中
                const elements = interval.elements;
                elements[0].setState('selected', true);
            },

            /**
             * 获取数据
             */
            getMonitorData(){
                var _this = this;
                NProgress.start();
                axios.get(Api.METRICS_URL)
                    .then(function (response) {
                        if(response.data.success){
                            var data = response.data.data;

                            //处理kafka 统计信息
                            if(data.counters){
                                var counters = [];
                                counters.push({ country: '已处理的binlog行数', population: data.counters['MaxwellMetrics.row.count'].count});
                                counters.push({ country: '成功发送到kafka的消息数量', population: data.counters['MaxwellMetrics.messages.succeeded'].count});
                                counters.push({ country: '发送失败的消息数量', population: data.counters['MaxwellMetrics.messages.failed'].count});
                                _this.counters = counters;
                            }

                            if(data.gauges){
                                //处理内存使用
                                var memoryUsageData = [];
                                var maxVal = data.gauges['MaxwellMetrics.jvm.memory_usage.total.max'].value;
                                var usedVal = data.gauges['MaxwellMetrics.jvm.memory_usage.total.used'].value;
                                var val = Number(((usedVal/maxVal) * 100).toFixed(3));
                                memoryUsageData.push({ type: '内存使用', value: val});
                                memoryUsageData.push({ type: '剩余内存', value: 100 - val});
                                _this.memoryUsageData = memoryUsageData;

                                var memoryData = [];
                                var val = data.gauges['MaxwellMetrics.jvm.memory_usage.total.max'].value;
                                memoryData.push({ type: '最大内存', value: Number((val/(1024*1024)).toFixed(0)) });
                                val = data.gauges['MaxwellMetrics.jvm.memory_usage.total.init'].value;
                                memoryData.push({ type: '可以使用内存', value: Number((val/(1024*1024)).toFixed(0)) });
                                val = data.gauges['MaxwellMetrics.jvm.memory_usage.total.used'].value;
                                memoryData.push({ type: '已经使用内存', value: Number((val/(1024*1024)).toFixed(0)) });
                                _this.memoryData = memoryData;
                            }
                        }
                        NProgress.done();
                    });
            },

            /**
             * 刷新
             */
            refreshEvent(){

                this.getMonitorData();
            }

        },
        watch:{
            counters(val){
                this.kafkaMonitorChart.data(val);
                this.kafkaMonitorChart.render();
            },

            memoryUsageData(){
                this.memoryUsageMonitorChart.data(this.memoryUsageData);
                this.memoryUsageMonitorChart.render();
            },

            memoryData(){
                this.memoryMonitorChart.data(this.memoryData);
                this.memoryMonitorChart.render();
            }
        }
    });
});