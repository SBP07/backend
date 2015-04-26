var speelsysteemControllers = angular.module('speelsysteemControllers', ['ui.bootstrap']);

speelsysteemControllers.controller('animatorsController', function($scope, $http) {
    $http.get('/api/animator/all')
        .success(function (response) {$scope.animators = response;});
});

speelsysteemControllers.controller('shiftsController', function($scope, $http) {
    $http.get('/api/shift/all')
        .success(function (response) {$scope.shifts = response;});
});

speelsysteemControllers.controller('childrenController', function($scope, $http) {
    $http.get('/api/child/all')
        .success(function (response) {$scope.children = response;});
});

speelsysteemControllers.controller('homeController', function() {

});

speelsysteemControllers.controller('navbarController', function() {

});
