angular.module("transCat",['ngRoute']).config(['$routeProvider',function($routeProvider){
    $routeProvider.when('/transList',{templateUrl:"../transInfo/transIndex",controller:"queryTrans"})
        .when("/transDetail/:id",{templateUrl:"../transInfo/transDetailPage",controller:"transDetail"}).otherwise({redirectTo:'/transList'});
}]);



var appModule = angular.module('app', []);
appModule.directive('hello', function() {
    return {
        restrict: 'E',
        template: '<div>Hi there</div>'
    };
});


angular.module("app",[]).directive("pageBar",function() {
    return {
        restrict: "E",
        template: "<h1>123123</h1>",
        replace:true
         /*<li><a>首页</a></li>" +
        "<li><a >上一页</a></li>"
        + "<li ng-repeat='p in pageCount' ng-click='transPage(p)'><a>{{p}}</a></li>" +
        "<li><a>下一页</a></li><li><a>尾页</a></li>"*/



    }

})
