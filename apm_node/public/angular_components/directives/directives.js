	'use strict';

	/* Directives */

	angular.module('myApp.directives', [])
		.directive('datePicker', [function(){
			return {
				restrict: 'EA',
				replace: true,
				templateUrl:"../views/tpls/datePicker.html"
			}
		}])
		.directive('datePicker2', [function(){
			return {
				restrict: 'EA',
				replace: true,
				templateUrl:"../views/tpls/datePicker2.html"
			}
		}])
