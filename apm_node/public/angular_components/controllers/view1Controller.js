/**
 * Created by dongxiaoyong on 2016/8/9 0009.
 */
(function () {
    angular.module('myApp.view1',[])
        .controller("AppView1Ctrl", ["$scope", "$rootScope","userService",function ($scope, $rootScope,userService) {

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
            var nowTime = new Date().format("yyyy-MM-dd");
            $scope.endTime=nowTime;
            $scope.queryFilter = {
                version: '',
                channel: '',
                //时间段
                interval:'7',
                //日周月
                timeFrame:'day',
                endTime:$scope.endTime
            };


            $scope.changeTimeFrame = function (timeFrame) {
                console.log('timeFrame:' + timeFrame);
                $scope.queryFilter.timeFrame = timeFrame;
                $scope.query();
            };

            var myChart = echarts.init(document.getElementById('main'));

            var option = {
                title: {
                    text: '新增用户趋势图'
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
                         data : []
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : []
            };

            userService.newadduser($scope.queryFilter).success(function(response) {
                var xAxis = [];
                var yAxis = [];
                for(var i=0;i<response.length;i++){
                    xAxis.push(response[i].date_time);yAxis.push(response[i].count_value);
                }
                $scope.postData = response;
                console.log(response);
                // 使用刚指定的配置项和数据显示图表。
                option.xAxis[0].data =xAxis;
                //option.legend.data = legends;
                option.series.push({
                    name:'',
                    type:'bar',
                    data:yAxis
                });
                myChart.setOption(option);
            });

            $scope.refresh = function (data) {
                myChart.setOption(data);
            };

            function  DateDiff(sDate1,  sDate2){    //sDate1和sDate2是2006-12-18格式
                var  aDate,  oDate1,  oDate2,  iDays
                aDate  =  sDate1.split("-")
                oDate1  =  new  Date(aDate[1]  +  '-'  +  aDate[2]  +  '-'  +  aDate[0])    //转换为12-18-2006格式
                aDate  =  sDate2.split("-")
                oDate2  =  new  Date(aDate[1]  +  '-'  +  aDate[2]  +  '-'  +  aDate[0])
                iDays  =  parseInt(Math.abs(oDate1  -  oDate2)  /  1000  /  60  /  60  /24)    //把相差的毫秒数转换为天数
                return  iDays
            }

            $scope.querySpan = function () {
                var startTime = new Date($scope.startTime).format("yyyy-MM-dd");
                $scope.queryFilter.endTime = new Date($scope.endTime).format("yyyy-MM-dd");
                var interval = DateDiff($scope.queryFilter.endTime,startTime);
                if (interval > 0) {
                    $scope.queryFilter.interval = interval
                }
                else {
                    $scope.queryFilter.interval = 0
                }
                $scope.query();
            };

            $scope.query = function () {
                // console.log($scope.queryFilter.timeSpan);
                console.log($scope.queryFilter.interval);
                userService.newadduser($scope.queryFilter).success(function (response) {
                    $scope.refresh(
                        {
                            xAxis: {
                                data: [Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100)]
                            },
                            series: {
                                data: [Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100), Math.floor(Math.random() * 100)]
                            }
                        }
                    );
                });
            };

            $scope.addCompare = function () {
                userService.newadduser($scope.queryFilter).success(function (response) {
                    option.series.push({
                        data:[]
                    });
                    myChart.setOption(option);
                });
            }
        }])
        .factory('userService', ['$http', function ($http) {
            var newadduser = function (data) {
                var path="http://localhost:8181/userManager/newAddUser";
                return $http.post(path,data);
            };
            return {
                newadduser: function (data) {
                    return newadduser(data);
                }
            }
        }]);
})();