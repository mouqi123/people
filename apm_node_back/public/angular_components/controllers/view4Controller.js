/**
 * Created by dongxiaoyong on 2016/8/9 0009.
 */
(function () {
    angular.module('myApp.view4',[])
        .controller("AppView4Ctrl", ["$scope", "$rootScope","pageService",function ($scope, $rootScope,pageService) {

            $scope.queryFilter = {
                version: '',
                channel: '',
                endTime:'',
                interval: ''
            };

            $scope.changeInterval = function (interval) {
                console.log('interval:' + interval);
                $scope.queryFilter.interval = interval;
                $scope.query();
            };

            var myChart = echarts.init(document.getElementById('main'));

            pageService.list($scope.queryFilter).success(function(response) {
                console.log(response);
                $scope.postData = response;
            });
            pageService.pagecount($scope.queryFilter).success(function(response) {
                //$scope.versionlist = response;

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
                            data:response
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
                pageService.list($scope.queryFilter).success(function (response) {
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
            var list = function (data) {
                var path="http://localhost:8181/userManager/getList";
                return $http.post(path,data);
            };
            var pagecount = function (data) {
                var path="http://localhost:8181/participationManager/pageCount";
                return $http.post(path,data);
            };
            return {
                list: function (data) {
                    return list(data);
                },
                pagecount: function (data) {
                    return pagecount(data);
                }
            }
        }]);
})();