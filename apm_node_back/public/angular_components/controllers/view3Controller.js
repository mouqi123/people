/**
 * Created by dongxiaoyong on 2016/8/9 0009.
 */
(function () {
    angular.module('myApp.view3',[])
        .controller("AppView3Ctrl", ["$scope", "$rootScope","daytimeService",function ($scope, $rootScope,daytimeService) {
            $scope.queryFilter = {
                version: '',
                channel: '',
                dayTime:''
            };


            //获取当前日期
            var myDate = new Date();
            var nowDay = myDate.getDate();
            var nowMonth = myDate.getMonth()+1;
            var nowDate = nowMonth+"-"+nowDay;

            var myChart = echarts.init(document.getElementById('main'));

            daytimeService.list($scope.queryFilter).success(function(response) {
                console.log(response);
                $scope.postData = response;
            });
            var option = {
                title: {
                    text: '日使用时长分布'
                },
                //color: ['#3398DB'],
                legend: {
                    data:[nowDate]
                },
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
                        type: 'category',
                        data: ['1-3秒', '4-10秒', '11-30秒', '31-60秒', '1-3分', '3-10分', '10-30分', '超过30分']
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
            daytimeService.dayusetime($scope.queryFilter).success(function(response) {

                console.log(nowDate);

                // 使用刚指定的配置项和数据显示图表。
                option.series.push({name:nowDate,type:'bar',data:response});
                myChart.setOption(option);
                $scope.refresh = function (data) {
                    myChart.setOption(data);
                 }
            });

            $scope.query = function () {
                daytimeService.list($scope.queryFilter).success(function (response) {
                    $scope.refresh(
                        {
                            series: {
                                data: [Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100)]
                            },
                            xAxis: {
                                data: [Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100)]
                            }
                        }
                    );
                });
            };

            $scope.addCompare = function () {
                daytimeService.list($scope.queryFilter).success(function (response) {
                    option.series.push({
                        name:$scope.queryFilter.dayTime,
                        type:'bar',
                        data: [Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100)]
                    });
                    myChart.setOption(option);
                });
            }
        }])
        .factory('daytimeService', ['$http', function ($http) {
            var list = function (data) {
                var path="http://localhost:8181/userManager/getList";
                return $http.post(path,data);
            };
            var dayusetime = function (data) {
                var path="http://localhost:8181/participationManager/dayUseTime";
                return $http.post(path,data);
            };
            return {
                list: function (data) {
                    return list(data);
                },
                dayusetime: function (data) {
                    return dayusetime(data);
                }
            }
        }]);
})();