/**
 * Created by dongxiaoyong on 2016/8/9 0009.
 */
(function () {
    angular.module('myApp.view5',[])
        .controller("AppView5Ctrl", ["$scope", "$rootScope","channelService",function ($scope, $rootScope,channelService) {

            $scope.queryFilter = {
                version: '',
                channel: '',
                timeSpan:'',
                userType:''
            };

            $scope.userDisplay = function (userType) {
                console.log('userType:' + userType);
                $scope.queryFilter.userType = userType;
                $scope.query();
            };

            var myChart = echarts.init(document.getElementById('main'));

            channelService.list($scope.queryFilter).success(function(response) {
                console.log(response);
                $scope.postData = response;
            });

            var option = {
                title: {
                    text: '日趋势变化'
                },
                tooltip : {
                    trigger: 'axis'
                },
                legend: {
                    data:['新增用户']
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

            channelService.newadduser($scope.queryFilter).success(function(response) {
                //$scope.versionlist = response;

                // 使用刚指定的配置项和数据显示图表。
                option.series.push({name:'',type:'line',data:response});
                myChart.setOption(option);
                $scope.refresh = function (data) {
                    myChart.setOption(data);
                }
            });

            $scope.query = function () {
                channelService.list($scope.queryFilter).success(function (response) {
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
                channelService.list($scope.queryFilter).success(function (response) {
                    option.series.push({
                        name:'',
                        type:'line',
                        data: [Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100)]
                    });
                    myChart.setOption(option);
                });
            }
        }])
        .factory('channelService', ['$http', function ($http) {
            var list = function (data) {
                var path="http://localhost:8181/userManager/getList";
                return $http.post(path,data);
            };
            var newadduser = function (data) {
                var path="http://localhost:8181/channelManager/newAddUser";
                return $http.post(path,data);
            };
            var activeuser = function (data) {
                var path="http://localhost:8181/channelManager/activeUser";
                return $http.post(path,data);
            };
            return {
                list: function (data) {
                    return list(data);
                },
                newadduser: function (data) {
                    return newadduser(data);
                },
                activeuser: function (data) {
                    return activeuser(data);
                }
            }
        }]);
})();