var app = angular.module('speelsysteemApp', []);

app.controller('animatorCtrl', function($scope, $http) {
    $http.get("/api/animator/all")
        .success(function (response) {$scope.animators = response;});
});

app.controller('shiftCtrl', function($scope, $http) {
    $http.get("/api/shift/all")
        .success(function (response) {$scope.shifts = response;});
});

app.controller('childrenCtrl', function($scope, $http) {
    $http.get("/api/child/all")
        .success(function (response) {$scope.children = response;});
});

