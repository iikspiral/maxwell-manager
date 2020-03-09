define([
    'vue',
    'axios',
    'g2',
    'api',
    'text!./home.html',
    'css!./home.css'
], function(Vue, axios,G2,Api,tempLate) {
    'use strict';
    return Vue.extend({
        template: tempLate,
        data() {
            return {
                chart:null,
                counters:[
                    { country: '成功发送到kafka的消息数量', population: 0 },
                    { country: '已处理的binlog行数', population: 0 },
                    { country: '发送失败的消息数量', population: 0 }
                ]
            }
        },
        mounted() {

            const chart = new G2.Chart({
                container: 'container',
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
            this.chart = chart;
            var _this = this;
            axios.get(Api.METRICS_URL)
                .then(function (response) {
                    if(response.data.success){
                        var data = response.data.data;
                        var counters = [];
                        counters.push({ country: '已处理的binlog行数', population: data.counters['MaxwellMetrics.row.count'].count});
                        counters.push({ country: '成功发送到kafka的消息数量', population: data.counters['MaxwellMetrics.messages.succeeded'].count});
                        counters.push({ country: '发送失败的消息数量', population: data.counters['MaxwellMetrics.messages.failed'].count});
                        _this.counters = counters;
                    }
                });
        },
        methods: {

        },
        watch:{
            counters(val){
                this.chart.data(val);
                this.chart.render();
            }
        }
    });
});