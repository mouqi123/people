/**
 * Created by dongxiaoyong on 2016/8/9 0009.
 */
(function () {
    angular.module('myApp.tpls',[])
        //左侧菜单
        .controller("MenuCtrl", ["$rootScope", "$scope", "$location", function ($rootScope,$scope,$location) {
        }])

        //日期插件
        .controller("DatepickerCtrl" ,['$scope', 'HandleDate',function ($scope, HandleDate) { //时间 input 控件
            $scope.today = function() {
                $scope.dt = new Date();
            };
            $scope.today();
            $scope.showWeeks = true;
            $scope.toggleWeeks = function () {
                $scope.showWeeks = ! $scope.showWeeks;
            };
            $scope.clear = function () {
                $scope.dt = null;
            };

            // Disable weekend selection
            $scope.disabled = function(date, mode) {
                if ($scope.datePickerController == 'trade' && mode == 'day' &&　 ( HandleDate.switchToDay(date) < 20160600 || ($scope.dt2 && !HandleDate.notGreaterThanAMonth(date, $scope.dt2)))) { //交易日志模块里

                    console.log(HandleDate.switchToDay(date));
                    console.log(HandleDate.switchToDay(date) < 20160600);

                    return true;
                }
                else if ($scope.dt2 && mode == 'day' && HandleDate.greaterThan(date, $scope.dt2)) {
                    return true;
                } else {
                    return ( mode == 'day' && ( date.getDay() == 0 || date.getDay() == 6 ) );
                }

                function switchDate(time) {
                    var year = time.getFullYear();
                    var month = time.getMonth() + 1;
                    var date = time.getDate();
                    if (month < 10) {
                        month = '0' + month;
                    }
                    if (date<10) {
                        date = '0' + date;
                    }
                    return  year + month + date;
                }
            };

            $scope.toggleMin = function() {
                $scope.minDate = ( $scope.minDate ) ? null : new Date();
            };
            $scope.toggleMin();

            $scope.open = function($event) {
                $event.preventDefault();
                $event.stopPropagation();

                $scope.opened = true;
            };

            $scope.dateOptions = {
                'year-format': "'yy'",
                'starting-day': 1
            };

            $scope.formats = ['yyyy-MM-dd', 'yyyy/MM/dd', 'shortDate'];
            $scope.format = $scope.formats[0];
        }])
        .controller("DatepickerCtrl2" ,['$scope', 'HandleDate',function ($scope, HandleDate) { //时间 input 控件
            $scope.today = function() {
                $scope.dt2 = new Date();
            };
            $scope.today();
            $scope.showWeeks = true;
            $scope.toggleWeeks = function () {
                $scope.showWeeks = ! $scope.showWeeks;
            };
            $scope.clear = function () {
                $scope.dt2 = null;
            };

            // Disable weekend selection   并且不小于开始时间
            $scope.disabled = function(date, mode) {

                if ($scope.datePickerController == 'trade' && mode == 'day' &&　 ( HandleDate.switchToDay(date) < 20160600 || ($scope.dt && !HandleDate.notLessThanAMonth(date, $scope.dt)))) { //交易日志模块里
                    return true;
                } else
                if ($scope.dt && mode == 'day' && HandleDate.lessThan(date, $scope.dt)) {
                    return true;
                } else {
                    return ( mode == 'day' && ( date.getDay() == 0 || date.getDay() == 6 ) );
                }
            };

            $scope.toggleMin = function() {
                $scope.minDate = ( $scope.minDate ) ? null : new Date();
            };
            $scope.toggleMin();

            $scope.open = function($event) {
                $event.preventDefault();
                $event.stopPropagation();

                $scope.opened = true;
            };

            $scope.dateOptions = {
                'year-format': "'yy'",
                'starting-day': 1
            };

            $scope.formats = ['yyyy-MM-dd', 'yyyy/MM/dd', 'shortDate'];
            $scope.format = $scope.formats[0];
        }])
})();