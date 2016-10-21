/**
 * Created by dongxiaoyong on 2016/8/1 0001.
 */
(function () {
    angular.module('myApp',['ngRoute','ui.bootstrap', 'myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.view1', 'myApp.view2', 'myApp.view3', 'myApp.view4', 'myApp.view5', 'myApp.view6', 'myApp.tpls'])
        .run(['$rootScope', '$location', function($rootScope, $location){

            //初始化左侧菜单数据以及切换菜单的方法

            $rootScope.menuList = [
                {
                    name: '用户分析',
                    active: true,
                    prefix: 'userAnalysis',
                    members: [
                        {
                            name: '新增用户',
                            url: 'view1',
                            active: false
                        },
                        {
                            name: '活跃用户',
                            url: 'view2',
                            active: false
                        }
                    ]
                },
                {
                    name: '用户参与度',
                    active: true,
                    prefix: 'userParticipation',
                    members: [
                        {
                            name: '日使用时长',
                            url: 'view3',
                            active: false
                        },
                        {
                            name: '访问页面',
                            url: 'view4',
                            active: false
                        }
                    ]
                },
                {
                    name: '渠道分析',
                    active: true,
                    prefix: 'channelAnalysis',
                    members: [
                        {
                            name: '渠道列表',
                            url: 'view5',
                            active: false
                        }
                    ]
                },
                {
                    name: '错误分析',
                    active: true,
                    prefix: 'errorAnalysis',
                    members: [
                        {
                            name: '错误趋势',
                            url: 'view6',
                            active: false
                        }
                    ]
                }
            ];

            //一级菜单点击事件
            $rootScope.toggleMenu1 = function (m1) {
                for (var n in $rootScope.menuList) {
                    $rootScope.menuList[n].active = ($rootScope.menuList[n].name == m1.name);
                }
            };

            //二级菜单点击事件
            $rootScope.toggleMenu2 = function (m1, m2) {

                for (var n in m1.members) {
                    m1.members[n].active = (m1.members[n].url ==  m2.url);
                }

                m2.active = true;
            };

            //监听路由变化，刷新菜单状态
            $rootScope.$on('$routeChangeSuccess', function(evt, next, previous) {
                var arr = $location.path().slice(1).split('/');

                var prefix = arr[0];
                var url = arr[1];

                for (var n in $rootScope.menuList) {
                    if ($rootScope.menuList[n].prefix == prefix) {
                        $rootScope.menuList[n].active = true;

                        for (var m in $rootScope.menuList[n]) {
                            $rootScope.menuList[n][m].active = ($rootScope.menuList[n][m].url ==  url)
                        }
                    } else {
                        $rootScope.menuList[n].active = false
                    }
                }
            });
        }])
        .config(['$routeProvider', function($routeProvider) {
            $routeProvider
                .when('/userAnalysis/view1', {
                    templateUrl: '/views/userAnalysis/view1/index.html',
                    controller: 'AppView1Ctrl'})
                .when('/userAnalysis/view2', {
                    templateUrl: '/views/userAnalysis/view2/index.html',
                    controller: 'AppView2Ctrl'
                })
                .when('/userParticipation/view3', {
                    templateUrl: '/views/userParticipation/view3/index.html',
                    controller: 'AppView3Ctrl'
                })
                .when('/userParticipation/view4', {
                    templateUrl: '/views/userParticipation/view4/index.html',
                    controller: 'AppView4Ctrl'
                })
                .when('/channelAnalysis/view5', {
                    templateUrl: '/views/channelAnalysis/view5/index.html',
                    controller: 'AppView5Ctrl'
                })
                .when('/errorAnalysis/view6', {
                    templateUrl: '/views/errorAnalysis/view6/index.html',
                    controller: 'AppView6Ctrl'})
                .otherwise({
                    redirectTo: 'userAnalysis/view1'
                });
        }])
        .factory('HandleDate', ['$http',function () {
            var switchToArr = function (time) {
                var year = time.getFullYear();
                var month = time.getMonth() + 1;
                var date = time.getDate();
                if (month < 10) {
                    month = '0' + month;
                }
                if (date<10) {
                    date = '0' + date;
                }
                return  [year , month , date];
            };

            return {
                switchToDay: function (time) {
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
                },
                greaterThan: function (time1, time2) {
                    return switchToArr(time1).join("") >= switchToArr(time2).join("");
                },
                lessThan: function (time1, time2) {
                    return switchToArr(time1) <= switchToArr(time2);
                },
                notLessThanAMonth: function (time1, time2) {
                    var time1Arr = switchToArr(time1),
                        time2Arr = switchToArr(time2);

                    return (time1Arr[0] == time2Arr[0] && time1Arr[1] == time2Arr[1] && time1Arr[2] > time2Arr[2])
                },
                notGreaterThanAMonth: function (time1, time2) {
                    var time1Arr = switchToArr(time1),
                        time2Arr = switchToArr(time2);

                    return (time1Arr[0] == time2Arr[0] && time1Arr[1] == time2Arr[1] && time1Arr[2] < time2Arr[2])
                }
            }
        }])
})();