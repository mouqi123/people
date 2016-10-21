/**
 * Created by dongxiaoyong on 2016/8/9 0009.
 */
(function () {
    angular.module('myApp.view6',[])
        .controller("AppView6Ctrl", ["$scope", "$rootScope","errorService",function ($scope, $rootScope,errorService) {

            $scope.queryFilter = {
                version: '',
                timeSpan:'',
                errorInfo:''
            };


            $scope.errorInfo = function (errorInfo) {
                console.log('errorInfo:' + errorInfo);
                $scope.queryFilter.errorInfo = errorInfo;
                $scope.query();
            };

            var myChart = echarts.init(document.getElementById('main'));

            errorService.list($scope.queryFilter).success(function(response) {
                console.log(response);
                $scope.postData = response;
            });

            var option = {
                title: {
                    text: '错误趋势'
                },
                tooltip : {
                    trigger: 'axis'
                },
                legend: {
                    data:['总错误数']
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

            errorService.errorList($scope.queryFilter).success(function(response) {


                // 使用刚指定的配置项和数据显示图表
                option.series.push({name:'',type:'line',data:response});
                myChart.setOption(option);
                $scope.refresh = function (data) {
                    myChart.setOption(data);
                }
            });

            $scope.query = function () {
                errorService.list($scope.queryFilter).success(function (response) {
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
                errorService.list($scope.queryFilter).success(function (response) {
                    option.series.push({
                        name:'',
                        type:'line',
                        data: [Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100)]
                    });
                    myChart.setOption(option);
                });
            }


        }])
        .factory('errorService', ['$http', function ($http) {
            var list = function (data) {
                var path="http://localhost:8181/errorManager/getList";
                return $http.post(path,data);
            };
            var errorList = function (data) {
                var path="http://localhost:8181/errorManager/errorList";
                return $http.post(path,data);
            };
            return {
                list: function (data) {
                    return list(data);
                },
                errorList: function (data) {
                    return errorList(data);
                }
            }
        }]);
})();