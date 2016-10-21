/**
 * Created by dongxiaoyong on 2016/8/9 0009.
 */
(function () {
    angular.module('myApp.view2',[])
        .controller("AppView2Ctrl", ["$scope", "$rootScope","activeService",function ($scope, $rootScope,activeService) {

            $scope.queryFilter = {
                version: '',
                channel: '',
                timeSpan:'',
                timeFrame: '',
                dayTime:''
            };

            $scope.changeTimeFrame = function (timeFrame) {
                console.log('timeFrame:' + timeFrame);
                $scope.queryFilter.timeFrame = timeFrame;
                $scope.query();
            };

            var myChart = echarts.init(document.getElementById('main'));

            activeService.list($scope.queryFilter).success(function(response) {
                console.log(response);
                $scope.postData = response;
            });

            var option = {
                title: {
                    text: '活跃用户趋势图'
                },
                tooltip : {
                    trigger: 'axis'
                },
                legend: {
                    data:['活跃用户']
                },
                toolbox: {
                    show: true,
                    orient: 'vertical',
                    left: 'right',
                    top: 'center',
                    feature: {
                        dataView: {readOnly: false},
                        saveAsImage: {}
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
                        boundaryGap : false,
                        data : ['周一','周二','周三','周四','周五','周六','周日']
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                ]
            };

            activeService.activeuser($scope.queryFilter).success(function(response) {

                // 使用刚指定的配置项和数据显示图表。
                option.series.push({name:'',type:'line',data:response});
                myChart.setOption(option);
                $scope.refresh = function (data) {
                    myChart.setOption(data);
                }
            });

            $scope.query = function () {
                activeService.list($scope.queryFilter).success(function (response) {
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
            };

            $scope.addCompare = function () {
                activeService.list($scope.queryFilter).success(function (response) {
                    option.series.push({
                        name:'',
                        type:'line',
                        data: [Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100)]
                    });
                    myChart.setOption(option);
                });
            }
        }])
        .factory('activeService', ['$http', function ($http) {
            var list = function (data) {
                var path="http://localhost:8181/userManager/getList";
                return $http.post(path,data);
            };
            var activeuser = function (data) {
                var path="http://localhost:8181/userManager/activeUser";
                return $http.post(path,data);
            };
            return {
                list: function (data) {
                    return list(data);
                },
                activeuser: function (data) {
                    return activeuser(data);
                }
            }
        }]);
})();