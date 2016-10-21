/**
 * Created by dongxiaoyong on 2016/8/9 0009.
 */
(function () {
    angular.module('myApp.view4',[])
        .controller("AppView4Ctrl", ["$scope", "$rootScope","pageService",function ($scope, $rootScope,pageService) {

            //获取当前日期
            Date.prototype.format = function (fmt) { //author: mackie
                var o = {
                    "M+": this.getMonth() + 1, //月份
                    "d+": this.getDate(), //日
                    "h+": this.getHours(), //小时
                    "m+": this.getMinutes(), //分
                    "s+": this.getSeconds(), //秒
                    "q+": Math.floor((this.getMonth() + 3) / 3), //季度
                    "S": this.getMilliseconds() //毫秒
                };
                if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
                for (var k in o)
                    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
                return fmt;
            };

            // var aaaa = new Date($scope.queryFilter.dayTime).format("yyyy-MM-dd");
            // console.log(aaaa);
            var nowTime = new Date().format("yyyy-MM-dd");

            $scope.queryFilter = {
                version: '',
                channel: '',
                endTime:nowTime,
                interval: '7'
            };


            $scope.changeInterval = function (interval) {
                console.log('interval:' + interval);
                $scope.queryFilter.interval = interval;
                $scope.query();
            };

            var myChart = echarts.init(document.getElementById('main'));

            pageService.pagecount($scope.queryFilter).success(function(response) {
                $scope.postData = response;
                console.log(response[0].one);
                option = {
                    title: {
                        text: '访问页面分布'
                    },
                    color: ['#3398DB'],
                    tooltip : {
                        trigger: 'axis',
                        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                        }
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis : [
                        {
                            type : 'category',
                            data : ['1-2次', '3-5次', '6-9次', '10-29次', '30-99次',  '超过100次'],
                            axisTick: {
                                alignWithLabel: true
                            }
                        }
                    ],
                    yAxis : [
                        {
                            type : 'value'
                        }
                    ],
                    series : [
                        {
                            name:'数量',
                            type:'bar',
                            barWidth: '60%',
                            data:[ response[0].one, 'response.RowDataPacket.6to9', 'response.RowDataPacket.10to29', 'response.RowDataPacket.30to99',  'response.RowDataPacket.100more']
                        }
                    ]
                };
                // 使用刚指定的配置项和数据显示图表。
                myChart.setOption(option);
                $scope.refresh = function (data) {
                    myChart.setOption(data);
                }
            });

            $scope.query = function () {
                pageService.pagecount($scope.queryFilter).success(function (response) {
                    $scope.refresh(
                        {
                            series: {
                                data: [Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100)]
                            },
                            xAxis: {
                                data: []
                            }
                        }
                    );
                });


            }

        }])
        .factory('pageService', ['$http', function ($http) {
            var pagecount = function (data) {
                var path="http://localhost:8181/participationManager/pageCount";
                return $http.post(path,data);
            };
            return {
                pagecount: function (data) {
                    return pagecount(data);
                }
            }
        }]);
})();