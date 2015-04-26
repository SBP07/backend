var app = angular.module('speelsysteemApp', ['ngRoute', 'speelsysteemControllers']);

app.config(function($routeProvider){
    $routeProvider.when('/', {
        templateUrl: 'assets/app/templates/home.html',
        controller: 'homeController'
    }).when('/animatoren', {
        templateUrl: 'assets/app/templates/animators.html',
        controller: 'animatorsController'
    }).when('/kinderen', {
        templateUrl: 'assets/app/templates/children.html',
        controller: 'childrenController'
    }).when('/dagdelen', {
        templateUrl: 'assets/app/templates/shifts.html',
        controller: 'shiftsController'
    }).otherwise({
        redirectTo: '/'
    });
});
